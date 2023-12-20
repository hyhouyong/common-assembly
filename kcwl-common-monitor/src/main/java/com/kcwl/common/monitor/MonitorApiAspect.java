package com.kcwl.common.monitor;

import com.kcwl.common.monitor.annotation.MonitorCostTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * @author ckwl
 */
@Slf4j
@Aspect
@Order(5)
@AllArgsConstructor
public class MonitorApiAspect {
    /**
     *
     */
    @Pointcut("@annotation(com.kcwl.common.monitor.annotation.MonitorCostTime)")
    public void costTimePointcut() {
    }

    @Around("costTimePointcut()")
    public Object monitorApiAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        MonitorCostTime monitorCostTime = method.getAnnotation(MonitorCostTime.class);
        Long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        Long endTime = System.currentTimeMillis();
        if ( monitorCostTime.enableLog() && log.isInfoEnabled() ) {
            log.info("{}.{} cost: {}ms", signature.getDeclaringTypeName(), signature.getName(), endTime - startTime);
        }
        return result;
    }
}
