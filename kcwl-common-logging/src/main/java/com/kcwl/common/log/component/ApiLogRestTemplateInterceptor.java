package com.kcwl.common.log.component;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.kcwl.common.log.entity.ApiLog;
import com.kcwl.common.log.enums.ApiLogType;
import com.kcwl.common.log.enums.RetryStatus;
import com.kcwl.common.log.impl.ApiLogService;
import com.kcwl.common.log.util.HttpClientUtil;
import com.kcwl.common.log.util.MultiDataSourceHelper;
import com.kcwl.common.log.util.ServletUtil;
import com.kcwl.ddd.infrastructure.session.SessionContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

/**
 * 重试请求 resetTemplate 拦截器
 * 成功不记录，不重试
 *
 * 失败定义:
 * - http != 200
 * - http == 200 && response.content-type == application/json && (code 以 200 结束 || code == 0)
 */
@Slf4j
@AllArgsConstructor
public class ApiLogRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final ApiLogService apiLogService;

    private final Executor apiLogExecutor;

    private final String serviceName;

    // 当前环境
    private final String env;

    private final String tenantDataSource;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            ClientHttpResponse response = execution.execute(request, body);
            try {
                logApiRequest(request, body, response, null);
            } catch (Exception e) {
                log.error("api_log_rest_intercept error: ", e);
            }
            return response;
        } catch (Exception executeException) {

            try {
                logApiRequest(request, body, null, executeException);
            } catch (Exception e) {
                log.error("api_log_rest_intercept error: ", e);
            }

            throw executeException;
        }
    }

    /**
     * 尝试记录对外请求
     *
     * @param response  响应，可能未成功接到响应
     * @param exception 请求失败
     */
    private void logApiRequest(HttpRequest request, byte[] body, @Nullable ClientHttpResponse response, @Nullable Exception exception) throws IOException {

        ApiLogContext.ContextConfig contextConfig = ApiLogContext.context.get();

        if (contextConfig == null) {
            return;
        }

        // 抛出异常时跳过
        if (exception != null && contextConfig.getIgnoreIfException()) {
            return;
        }

        ApiLog.ApiLogBuilder builder = ApiLog.builder();

        // http 级别失败
        boolean failed = exception != null || response == null || response.getRawStatusCode() != 200;
        builder.responseCode(response == null ? null : response.getRawStatusCode());

        // 可以多次获取 Response.body 前提下，业务级别失败判断（ResponseMessage.code）
        if (response != null && "org.springframework.http.client.BufferingClientHttpResponseWrapper".equals(response.getClass().getName())) {

            MediaType responseContentType = response.getHeaders().getContentType();
            if (APPLICATION_JSON.equals(responseContentType) || APPLICATION_JSON_UTF8.equals(responseContentType)) {
                JSONObject responseJson = HttpClientUtil.getResponseJson(response);
                Object responseCode = responseJson.get("code");
                if (responseCode instanceof String) {
                    failed = failed || !ServletUtil.isResponseMessageSuccess((String) responseCode);
                }
                builder.responseBody(responseJson.toString());
            }
        }

        // 对外请求异常类型，多出现找不到服务
        if (exception != null) {
            builder.exceptionType(StrUtil.subSufByLength(exception.getClass().getName(), 75))
                    .exceptionMessage(StrUtil.subSufByLength(exception.getMessage(), 200));
        }

        // 接口请求失败，记录到数据库
        if (failed) {

            builder
                    .type(contextConfig.getRetryEnabled() ? ApiLogType.MANUAL_RETRY : ApiLogType.MANUAL_LOG)
                    .bizCode(contextConfig.getBizCode())
                    .env(env) // 当前环境
                    .serviceName(serviceName) // 当前服务名称
                    .destServiceName(request.getURI().getHost()) // 目标服务名称，非当前服务
                    .traceId(TraceContext.traceId())
                    .requestMethod(request.getMethodValue())
                    .requestUri(request.getURI().getPath())
                    .requestHeaders(HttpClientUtil.getRequestHeaderJson(request))
                    .retryUrl(request.getURI().toString()) // get param 不拆了，直接扔里面，下面代码也要注意保证 requestParam 只有在 form 时有值
                    ;

            // 平台信息相关
            Optional.ofNullable(SessionContext.getRequestUserAgent()).ifPresent(requestUserAgent -> {
                builder.platformNo(requestUserAgent.getPlatform()).product(requestUserAgent.getProduct());
            });

            // 支持 GET、POST(application/json、form)
            boolean unSupportRetry = false;

            // 重试参数相关
            if (HttpClientUtil.isPostMethod(request)) {

                MediaType requestContentType = request.getHeaders().getContentType();

                // application/json
                if (MediaType.APPLICATION_JSON.equals(requestContentType) || MediaType.APPLICATION_JSON_UTF8.equals(requestContentType)) {
                    builder.contentType(MediaType.APPLICATION_JSON_VALUE).body(new String(body, StandardCharsets.UTF_8));
                } else if (requestContentType != null && "application".equals(requestContentType.getType()) && "x-www-form-urlencoded".equals(requestContentType.getSubtype())) {
                    builder.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).requestParam(HttpClientUtil.formBody2Json(new String(body)));
                } else {
                    unSupportRetry = true;
                }
            } else if (!HttpClientUtil.isGetMethod(request)) {
                unSupportRetry = true;
            }

            // 重试相关（主动开启重试，且支持重试）
            if (!contextConfig.getRetryEnabled() || unSupportRetry) {
                builder.retryStatus(RetryStatus.LOG_ONLY);
            } else {
                builder.retryStatus(RetryStatus.PROCESSING) // 开启重试
                        .retryTimes(contextConfig.getRetryTimes())
                        .requestMethod(request.getMethodValue())
                        .retryWaitStrategy(contextConfig.getRetryWaitStrategy())
                        .retryCount(0)
                        .retryWaitInterval(contextConfig.getRetryWaitInterval());
            }

            apiLogExecutor.execute(() -> {
                // 默认使用主数据源，或者手动切换
                if (StrUtil.isBlank(tenantDataSource)) {
                    apiLogService.saveLog(builder.build());
                } else {
                    MultiDataSourceHelper.switchAndRun(tenantDataSource, () -> apiLogService.saveLog(builder.build()));
                }
            });

        }

    }

}
