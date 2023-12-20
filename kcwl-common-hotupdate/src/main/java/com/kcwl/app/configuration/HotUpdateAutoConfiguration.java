package com.kcwl.app.configuration;

import com.kcwl.aid.interfaces.api.ErrorPromptService;
import com.kcwl.app.response.DefaultErrorPromptDecorator;
import com.kcwl.ddd.infrastructure.api.IErrorPromptDecorator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 自动配置类
 * </p>
 *
 * @author renyp
 * @since 2023/2/28 20:51
 */
@Configuration
public class HotUpdateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IErrorPromptDecorator.class)
    public IErrorPromptDecorator defaultErrorPromptDecorator(ErrorPromptService errorPromptService) {
        return new DefaultErrorPromptDecorator(errorPromptService);
    }


}
