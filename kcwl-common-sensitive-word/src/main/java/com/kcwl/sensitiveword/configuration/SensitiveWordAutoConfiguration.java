package com.kcwl.sensitiveword.configuration;

import com.kcwl.sensitiveword.annotations.SensitiveWordScanAspect;
import com.kcwl.sensitiveword.datasource.DataSourceAdapter;
import com.kcwl.sensitiveword.datasource.FileDataSourceAdapter;
import com.kcwl.sensitiveword.datasource.MixedDataSourceAdapter;
import com.kcwl.sensitiveword.provider.AutomatonNfaSensitiveWordScanProvider;
import com.kcwl.sensitiveword.provider.SensitiveWordScanProvider;
import com.kcwl.sensitiveword.service.LocalNfaAcSensitiveWordScanService;
import com.kcwl.sensitiveword.service.SensitiveWordScanService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;

/**
 * <p>
 * 敏感词自动配置类
 * </p>
 *
 * @author renyp
 * @since 2023/5/26 14:29
 */

@Configuration
@ConditionalOnProperty(value = "kcwl.common.web.sensitiveWord.enable", havingValue = "true", matchIfMissing = false)
public class SensitiveWordAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "kcwl.assembly.sensitive.word.scan.data.source.file.enable", havingValue = "true", matchIfMissing = true)
    public FileDataSourceAdapter registerFileDataSourceAdapter() {
        return new FileDataSourceAdapter();
    }

    @Bean
    public MixedDataSourceAdapter registerMixedDataSourceAdapter(List<DataSourceAdapter> dataSourceAdapterList) {
        return new MixedDataSourceAdapter(dataSourceAdapterList);
    }

    @Bean
    @ConditionalOnMissingBean(SensitiveWordScanService.class)
    public SensitiveWordScanService registerSensitiveWordService(MixedDataSourceAdapter mixedDataSourceAdapter) {
        return new LocalNfaAcSensitiveWordScanService(mixedDataSourceAdapter);
    }

    @Bean("automatonNfaSensitiveWordScanProvider")
    @ConditionalOnProperty(value = "kcwl.assembly.sensitive.word.scan.service.nfa.automaton.enable", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(SensitiveWordScanProvider.class)
    public SensitiveWordScanProvider registerSensitiveWordScanProvider(SensitiveWordScanService sensitiveWordScanService) {
        // 2023/5/29 用户通过入参 路由到本地 和 远程词库，还是项目启动 根据环境变量 决定加载本地还是远程词库？
        // TODO: 2023/6/5 还是项目启动 根据环境变量 决定加载本地还是远程词库，远程词库的匹配服务实例 待实现..
        return new AutomatonNfaSensitiveWordScanProvider(sensitiveWordScanService);
    }

    @Bean
    @ConditionalOnBean(SensitiveWordScanProvider.class)
    public SensitiveWordScanAspect registerSensitiveWordScanAspect(SensitiveWordScanProvider sensitiveWordScanProvider) {
        return new SensitiveWordScanAspect(sensitiveWordScanProvider, new SpelExpressionParser());
    }

}
