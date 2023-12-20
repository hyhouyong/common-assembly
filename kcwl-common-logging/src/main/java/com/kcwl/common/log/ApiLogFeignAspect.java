package com.kcwl.common.log;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.kcwl.common.log.component.ApiLogContext;
import com.kcwl.common.log.entity.ApiLog;
import com.kcwl.common.log.enums.ApiLogType;
import com.kcwl.common.log.enums.RetryStatus;
import com.kcwl.common.log.impl.ApiLogService;
import com.kcwl.common.log.util.FeignUtil;
import com.kcwl.common.log.util.MultiDataSourceHelper;
import com.kcwl.common.log.util.ServletUtil;
import com.kcwl.ddd.infrastructure.session.SessionContext;
import feign.Request;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

/**
 * 对外请求 feign 切面
 */
@Slf4j
@Aspect
@Order(6)
@AllArgsConstructor
public class ApiLogFeignAspect {

    // 线程池
    private final Executor apiLogExecutor;

    // service
    private final ApiLogService apiLogService;

    // 当前服务名称
    private final String serviceName;

    // 当前环境
    private final String env;

    // 指定的 DataSource，空则使用默认
    private final String tenantDataSource;

    // 是否强制转换 无法重复读取的 feign.Response
    private final boolean convertNotRepeatableResponse;

    @Pointcut("execution(* feign.Client.execute(..))")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Exception executeException = null;

        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            executeException = e;
        }

        try {
            logFeignRequest(joinPoint, result, executeException);
        } catch (Exception e) {
            log.error("api_log_feign_aspect error: ", e);
        }

        if (executeException != null) {
            throw executeException;
        }

        return result;
    }

    /**
     * 转换 ResponseBody 为重复读取
     *
     * 1. api_log 开关开启
     * 2. 打开强制转换 Response 开关
     * 3. response 状态码非 200
     * 4. response 不可重复读取 {@link feign.Response.Body#isRepeatable()}
     * 5. response content-type 为 application/json
     *
     * @return 返回转换后的 Response 或 原入参
     */
    public Object convertResponseAsRepeatable(Object result) {

        // 开关
        if (!convertNotRepeatableResponse) {
            return result;
        }

        if (result instanceof Response) {

            try {
                Response response = (Response) result;

                if (response.status() != 200
                        && response.body() != null && !response.body().isRepeatable()
                        && FeignUtil.contentTypeContainsAny(response, APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE)) {
                    return Response.builder()
                            .status(response.status())
                            .reason(response.reason())
                            .headers(response.headers())
                            .request(response.request())
                            .body(IoUtil.readBytes(response.body().asInputStream()))
                            .build();
                }
            } catch (Exception e) {
                log.warn("api_log_feign_aspect convert Response.body error", e);
            }
        }
        return result;
    }

    /**
     *  尝试记录 feign 对外请求
     *  如果读取不到上下文，则跳过
     *  仅记录明确失败
     */
    public void logFeignRequest(ProceedingJoinPoint joinPoint, @Nullable Object result, @Nullable Exception exception) {

        ApiLogContext.ContextConfig contextConfig = ApiLogContext.context.get();

        if (contextConfig == null) {
            return;
        }

        // 抛出异常时跳过
        if (exception != null && contextConfig.getIgnoreIfException()) {
            return;
        }

        // 尝试转换成可重复读 Response
        if (exception == null) {
            result = convertResponseAsRepeatable(result);
        }

        Object[] args = joinPoint.getArgs();
        if (args.length != 2 || !(args[0] instanceof Request) || (result != null && !(result instanceof Response))) {
            return;
        }

        Request request = (Request) args[0];
        Response response = (Response) result;

        ApiLog.ApiLogBuilder builder = ApiLog.builder();

        // 先判断响应是否失败
        boolean responseFailed = exception != null || response == null || response.status() != 200;
        builder.responseCode(response == null ? null : response.status());

        if (response != null) {
            String responseContentType = FeignUtil.getContentType(response);
            if (APPLICATION_JSON_VALUE.equals(responseContentType) || APPLICATION_JSON_UTF8_VALUE.equals(responseContentType)) {
                JSONObject responseJson = FeignUtil.getBodyJsonObject(response);
                Object responseCode = responseJson.get("code");
                if (responseCode instanceof String) {
                    responseFailed = responseFailed || !ServletUtil.isResponseMessageSuccess((String) responseCode);
                }
                builder.responseBody(responseJson.toString());
            }
        }

        if (exception != null) {
            builder.exceptionType(StrUtil.subSufByLength(exception.getClass().getName(), 75))
                    .exceptionMessage(StrUtil.subSufByLength(exception.getMessage(), 200));
        }

        // 接口请求失败，记录到数据库
        if (responseFailed) {

            Optional<URI> requestURI = FeignUtil.parseUrl(request);

            builder
                    .type(contextConfig.getRetryEnabled() ? ApiLogType.MANUAL_RETRY : ApiLogType.MANUAL_LOG)
                    .bizCode(contextConfig.getBizCode())
                    .env(env) // 当前环境
                    .serviceName(serviceName) // 当前服务名称
                    .destServiceName(requestURI.map(URI::getHost).orElse(null)) // 目标服务名称，非当前服务
                    .traceId(TraceContext.traceId())
                    .requestMethod(request.httpMethod().name())
                    .requestUri(requestURI.map(URI::getPath).orElse(null))
                    .requestHeaders(FeignUtil.getRequestHeaderJson(request))
                    .retryUrl(request.url());

            // 平台信息相关
            Optional.ofNullable(SessionContext.getRequestUserAgent()).ifPresent(requestUserAgent -> {
                builder.platformNo(requestUserAgent.getPlatform()).product(requestUserAgent.getProduct());
            });

            // 支持 GET、POST(application/json、form)
            boolean unSupportRetry = false;

            // 重试时的相关参数
            if (FeignUtil.isPostMethod(request)) {

                String requestContentType = FeignUtil.getContentType(request);

                // application/json 或 form
                if (APPLICATION_JSON_VALUE.equals(requestContentType) || APPLICATION_JSON_UTF8_VALUE.equals(requestContentType)) {
                    builder.contentType(APPLICATION_JSON_VALUE).body(FeignUtil.getBodyJsonString(request));
                } else if (StrUtil.startWith(requestContentType, APPLICATION_FORM_URLENCODED_VALUE)) {
                    // 可能为 application/x-www-form-urlencoded;charset=UTF-8
                    builder.contentType(APPLICATION_FORM_URLENCODED_VALUE).requestParam(FeignUtil.formBody2Json(request));
                } else {
                    unSupportRetry = true;
                }
            } else if (!FeignUtil.isGetMethod(request)) {
                unSupportRetry = true;
            }


            // 是否开启重试相关
            if (!contextConfig.getRetryEnabled() || unSupportRetry) {
                builder.retryStatus(RetryStatus.LOG_ONLY);
            } else {
                builder.retryStatus(RetryStatus.PROCESSING)
                        .retryTimes(contextConfig.getRetryTimes())
                        .requestMethod(request.httpMethod().name())
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
