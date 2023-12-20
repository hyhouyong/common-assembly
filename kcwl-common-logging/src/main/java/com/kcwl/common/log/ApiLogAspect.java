package com.kcwl.common.log;

import cn.hutool.core.util.StrUtil;
import com.kcwl.common.log.annotation.ApiLogger;
import com.kcwl.common.log.entity.ApiLog;
import com.kcwl.common.log.enums.ApiLogType;
import com.kcwl.common.log.enums.RetryStatus;
import com.kcwl.common.log.impl.ApiLogService;
import com.kcwl.common.log.util.MultiDataSourceHelper;
import com.kcwl.common.log.util.ServletUtil;
import com.kcwl.ddd.infrastructure.session.SessionContext;
import com.kcwl.framework.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * Controller 切面
 * 接口提供方
 */
@Slf4j
@Aspect
@Order(4) // TODO: 2021/8/18 部分切面问题，会将异常修改为 RuntimeException
@AllArgsConstructor
public class ApiLogAspect {

    // 防止循环调用
    private final static ThreadLocal<Object> threadLocal = new InheritableThreadLocal<>();
    private final static Object EMPTY_OBJECT = new Object();

    // application
    private final String serviceName;

    // 当前环境
    private final String env;

    // 线程池
    private final Executor apiLogExecutor;

    // service
    private final ApiLogService apiLogService;

    // 指定的 DataSource，空则使用默认
    private final String tenantDataSource;

    @Pointcut("@annotation(com.kcwl.common.log.annotation.ApiLogger)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {

            // 线程内 防止嵌套调用
            if (e instanceof Exception
                    && threadLocal.get() == null) {
                try {
                    threadLocal.set(EMPTY_OBJECT);
                    try {
                        logServletRequest(joinPoint, (Exception) e);
                    } catch (Exception logE) {
                        log.error("api_log_servlet_aspect error: ", logE);
                    }
                } finally {
                    threadLocal.remove();
                }
            }
            throw e;
        }
    }

    public void logServletRequest(ProceedingJoinPoint joinPoint, Exception e) {
        HttpServletRequest request = ServletUtil.getRequest();
        if (request == null) {
            return;
        }

        // 检查 header，是否为重试
        if (StrUtil.isNotBlank(request.getHeader(ApiLogService.IS_RETRY_HEADER))) {
            return;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ApiLogger apiLog = method.getAnnotation(ApiLogger.class);


        // 限定了异常类型集合，但是没有一个匹配
        Class<? extends Exception>[] exceptions = apiLog.logIfException();
        if (exceptions.length > 0
                && Arrays.stream(exceptions).noneMatch(exception -> exception.isInstance(e))) {
            return;
        }

        // 基础字段
        ApiLog.ApiLogBuilder builder = ApiLog.builder()
                .type(ApiLogType.FAIL)
                .bizCode(apiLog.bizCode())
                .env(env) // 当前环境
                // 作为接口提供方，不一定能拿到请求方 serviceName
                .destServiceName(serviceName) // 目标服务名称，就是接口提供方，就是自己
                .traceId(TraceContext.traceId())
                .exceptionType(StrUtil.subSufByLength(e.getClass().getName(), 75))
                .exceptionMessage(StrUtil.subSufByLength(e.getMessage(), 200))
                .requestMethod(request.getMethod())
                .requestUri(request.getRequestURI());


        // 平台信息相关
        Optional.ofNullable(SessionContext.getRequestUserAgent()).ifPresent(requestUserAgent -> {
            builder.platformNo(requestUserAgent.getPlatform()).product(requestUserAgent.getProduct());
        });

        // 部分限制导致 不支持的重试操作，比如 `multipart/form-data`
        boolean unSupportRetry = false;

        // 记录全部请求头
        builder.requestHeaders(ServletUtil.getRequestHeaderJson(request));

        // GET
        if (ServletUtil.isGetMethod(request)) {
            builder.requestParam(ServletUtil.getParamMapJson(request));
        }  else if (ServletUtil.isPostMethod(request))  {

            String contentType = ServletUtil.getHeaderIgnoreCase(request, HttpHeaders.CONTENT_TYPE);

            // application/json
            if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType) || MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(contentType)) {
                builder.contentType(MediaType.APPLICATION_JSON_VALUE).requestParam(ServletUtil.getParamMapJson(request)).body(ServletUtil.getCachedRequestBody(request));
            } else if (StrUtil.startWith(contentType, MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                builder.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE).requestParam(ServletUtil.getParamMapJson(request));
            } else {
                unSupportRetry = true; // 不支持的 content-type
            }
        } else {
            unSupportRetry = true; // 不支持 GET POST 以外方法
        }

        // 是否开启重试
        if (!apiLog.retryEnabled() || unSupportRetry) {
            builder.retryStatus(RetryStatus.LOG_ONLY);
        } else {
            builder.retryStatus(RetryStatus.PROCESSING)
                    .retryTimes(Math.min(apiLog.retryTimes(), 20))
                    .retryMethod(StringUtil.isNotBlank(apiLog.retryMethod()) ? apiLog.retryMethod() : request.getMethod())
                    .retryUrl(StrUtil.isNotBlank(apiLog.retryUrl()) ? apiLog.retryUrl() : String.format("http://%s/%s", serviceName, request.getRequestURI()))
                    .retryWaitStrategy(apiLog.retryWaitStrategy())
                    .retryCount(0)
                    .retryWaitInterval(apiLog.retryWaitInterval());
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
