package com.kcwl.common.log;

import com.kcwl.common.log.impl.DbOpLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class LogAutoConfiguration {

    @Value("${spring.application.name}")
    String appName;

    @Value("${kcwl.common.log.coreSize:4}")
    int  coreSize=4;
    @Value("${kcwl.common.log.maxSize:8}")
    int  maxSize=8;
    @Value("${kcwl.common.log.capacity:1000}")
    int  capacity=1000;
    @Value("${kcwl.common.log.capacity:300}")
    int  keepAliveSeconds=300;

    @Resource
    DbOpLogger dbOpLogger;

    @Bean
    public OpLogAspect opLogAspect() {
        OpLogAspect opLogAspect = new OpLogAspect();
        opLogAspect.setOpLogger(dbOpLogger);
        opLogAspect.setAppName(appName);
        return opLogAspect;
    }

    @Bean(name = "loggerExecutor")
    public Executor loggerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(this.coreSize);
        executor.setMaxPoolSize(this.maxSize);
        executor.setQueueCapacity(this.capacity);
        executor.setKeepAliveSeconds(this.keepAliveSeconds);
        executor.setThreadNamePrefix("loggerExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(300);
        return executor;
    }
}
