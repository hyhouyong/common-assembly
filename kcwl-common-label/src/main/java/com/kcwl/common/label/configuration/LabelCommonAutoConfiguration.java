package com.kcwl.common.label.configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 自配配置类
 * </p>
 *
 * @author renyp
 * @since 2023/4/6 11:36
 */

@Configuration
@ComponentScan(basePackages = {"com.kcwl.common.label"})
@MapperScan(basePackages = {"com.kcwl.common.label.mapper"})
public class LabelCommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler registerMetaObjectHandler() {
        return new DefaultInsertOrUpdateFieldHandler();
    }
}
