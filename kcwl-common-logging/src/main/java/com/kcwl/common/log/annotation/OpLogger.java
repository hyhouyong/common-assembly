package com.kcwl.common.log.annotation;

import com.kcwl.common.log.LogLevel;
import com.kcwl.common.log.LogMode;
import com.kcwl.common.log.enums.OperTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpLogger {
    String message() default "";
    String level() default LogLevel.INFO;
    String mode() default LogMode.DETAIL;
    String tag() default "";
    OperTypeEnum operType() default OperTypeEnum.DEFAULT;
}
