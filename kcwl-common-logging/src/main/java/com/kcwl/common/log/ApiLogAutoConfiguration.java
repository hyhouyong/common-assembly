package com.kcwl.common.log;

import com.kcwl.common.log.component.ApiLogRestTemplateInterceptor;
import com.kcwl.common.log.impl.ApiLogService;
import com.kcwl.framework.rest.CommonWebAutoConfiguration;
import lombok.Data;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 接口异常日志切面 自动装配
 */
@Configuration
@EnableConfigurationProperties(ApiLogAutoConfiguration.ApiLogExecutorProperties.class)
@ConditionalOnProperty(prefix = "kcwl.common.api-log", value = "enabled", havingValue = "true")
@AutoConfigureAfter(CommonWebAutoConfiguration.class)
public class ApiLogAutoConfiguration {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${kcwl.common.api-log.env:#{null}}")
    private String env;

    // 指定切换 DataSource
    @Value("${kcwl.common.api-log.tenant-data-source:#{null}}")
    private String tenantDataSource;

    // 是否强制转换 无法重复读取的 feign.Response
    @Value("${kcwl.common.api-log.convertNotRepeatableFeignResponse:#{null}}")
    private String convertNotRepeatableResponse;

    /**
     * 异常接口记录切面（接收请求）
     */
    @Bean
    public ApiLogAspect apiLogAspect(Executor apiLogExecutor, ApiLogService apiLogService) {
        return new ApiLogAspect(serviceName, env, apiLogExecutor, apiLogService, tenantDataSource);
    }

    /**
     * 对外 feign 请求切面
     */
    @Bean
    public ApiLogFeignAspect apiLogFeignAspect(Executor apiLogExecutor, ApiLogService apiLogService) {
        return new ApiLogFeignAspect(apiLogExecutor, apiLogService, serviceName, env, tenantDataSource,
                "true".equals(convertNotRepeatableResponse));
    }

    /**
     * 对外 feignRestTemplate 拦截器
     */
    @Bean
    public ApiLogRestTemplateInterceptor apiLogRestTemplateInterceptor(ApiLogService apiLogService, Executor apiLogExecutor) {
        return new ApiLogRestTemplateInterceptor(apiLogService, apiLogExecutor, serviceName, env, tenantDataSource);
    }

    /**
     * 对外 feignRestTemplate 拦截配置
     */
    @Bean
    @ConditionalOnBean(name = "feignRestTemplate")
    public SmartInitializingSingleton apiLogFeignRestTemplateInterceptConfig(@Qualifier("feignRestTemplate") RestTemplate feignRestTemplate,
                                                                             ApiLogRestTemplateInterceptor apiLogRestTemplateInterceptor) {
        return () -> {
            feignRestTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
            feignRestTemplate.getInterceptors().add(apiLogRestTemplateInterceptor);
        };
    }

    /**
     * 上传文件线程池配置
     */
    @Data
    @ConfigurationProperties(prefix = "kcwl.common.api-log")
    public static class ApiLogExecutorProperties {
        int coreSize = 4;
        int maxSize = 4;
        int capacity = 1000;
        int keepAliveSeconds = 300;
    }

    /**
     * api log 线程池
     */
    @Bean
    public Executor apiLogExecutor(ApiLogExecutorProperties apiLogExecutorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(apiLogExecutorProperties.getCoreSize());
        executor.setMaxPoolSize(apiLogExecutorProperties.getMaxSize());
        executor.setQueueCapacity(apiLogExecutorProperties.getCapacity());
        executor.setKeepAliveSeconds(apiLogExecutorProperties.getKeepAliveSeconds());
        executor.setThreadNamePrefix("apiLogExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy()); // 队列满直接抛弃
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(300);
        return executor;
    }

}
