package com.kcwl.sensitiveword.annotations;

import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * <p>
 * 敏感词检测 声明式 入口
 * </p>
 *
 * @author renyp
 * @since 2023/6/1 17:11
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface SensitiveWordScanner {

    @AliasFor(attribute = "text")
    String value() default "";

    @AliasFor(attribute = "value")
    String text() default "";

    String[] textGenerator() default {};

    SensitiveLevelEnum level() default SensitiveLevelEnum.NORMAL;

}
