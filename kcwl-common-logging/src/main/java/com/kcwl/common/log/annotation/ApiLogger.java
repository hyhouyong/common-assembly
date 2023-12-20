package com.kcwl.common.log.annotation;

import com.kcwl.common.log.enums.RetryWaitStrategies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口级别异常
 * 可选重试执行
 * 重试适用范围，不影响主业务情况下
 *
 * 暂时没有拿当前 权限相关上下文
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLogger {

    /**
     * 限定生效异常（包含记录和重试）
     */
    Class<? extends Exception>[] logIfException() default {};

    /**
     * 业务标识（无功能作用，仅作区分）
     *
     * 尽量保持唯一，且不同
     */
    String bizCode() default "";

    /**
     * 是否开启重试
     */
    boolean retryEnabled() default false;

    /**
     * 重试次数
     */
    int retryTimes() default 3;

    /**
     * 重试时间间隔
     */
    int retryWaitInterval() default 60;

    /**
     * 重试等待间隔策略
     */
    RetryWaitStrategies retryWaitStrategy() default RetryWaitStrategies.FIXED;

    /**
     * 重试请求方法
     * 默认取当前，显式指定 retryUrl 时生效
     */
    String retryMethod() default "";

    /**
     * 重试地址，绝对路径
     *
     * 不传则取当前请求重试
     * <协议类型>://<spring.application.name>/<request_uri>
     */
    String retryUrl() default "";


}
