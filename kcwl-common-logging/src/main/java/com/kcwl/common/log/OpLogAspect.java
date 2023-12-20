package com.kcwl.common.log;

import com.kcwl.common.log.pojo.LogJoinPointContext;
import com.kcwl.common.log.annotation.OpLogger;
import com.kcwl.ddd.infrastructure.session.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import java.lang.reflect.Method;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

/**
 * @author ckwl
 */
@Aspect
@Slf4j
@Order(5)
public class OpLogAspect {
    private final static String EMPTY_STRING = "";

    private IOpLogger logger;
    private String appName;

    public OpLogAspect() {

    }

    public void setOpLogger(IOpLogger logger) {
        this.logger = logger;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(com.kcwl.common.log.annotation.OpLogger)")
    public void logPointcut() {
    }

    /**
     * @Description 配置环绕通知,使用在方法logPointcut()上注册的切入点
     * @Author  xueyz
     * @Date   2019/12/4 18:31
     * @Param  [joinPoint]
     * @Return      java.lang.Object
     * @Exception
     *
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Exception {
        Object result = null;
        Exception exception=null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            if (e instanceof Exception) {
                exception = (Exception)e;
            } else {
                exception = new RuntimeException(e.getMessage());
            }
        }

        if ( logger != null ) {
            logger.record(getLogJoinPointInfo(joinPoint, result, exception));
        } else {
            log.warn("Logger is null!");
        }

        //如果执行方法出现异常继续往外抛出
        if ( exception != null ) {
            throw exception;
        }

        return result;
    }

    private LogJoinPointContext getLogJoinPointInfo(ProceedingJoinPoint joinPoint, Object result, Exception exception) {
        LogJoinPointContext logJoinPointInfo = new LogJoinPointContext();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OpLogger aopOpLogger = method.getAnnotation(OpLogger.class);
        logJoinPointInfo.setAopOpLogger(aopOpLogger);
        logJoinPointInfo.setServiceName(this.appName);
        logJoinPointInfo.setMethodName(method.getName());
        logJoinPointInfo.setException(exception);
        logJoinPointInfo.setResult(result);
        logJoinPointInfo.setArgs(joinPoint.getArgs());
        logJoinPointInfo.setSessionData(SessionContext.getSessionData());
        logJoinPointInfo.setRequestUserAgent(SessionContext.getRequestUserAgent());
        logJoinPointInfo.setTraceId(TraceContext.traceId());
        return logJoinPointInfo;
    }
}
