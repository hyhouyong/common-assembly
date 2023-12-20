package com.kcwl.common.monitor;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ckwl
 */
@Configuration
public class MonitorConfiguration {

    private static final int CACHE_INITIAL_CAPACITY = 1000;

    @Value("${kcwl.alarm.interval:30}")
    private int interval;
    @Value("${kcwl.alarm.coreSize:2}")
    int  coreSize;
    @Value("${kcwl.alarm.maxSize:8}")
    int  maxSize;
    @Value("${kcwl.alarm.capacity:1000}")
    int  capacity;
    @Value("${kcwl.alarm.capacity:300}")
    int  keepAliveSeconds;

    @Bean(name = "recentAlarmException")
    public Cache<String, Long> recentAlarmException() {
        return Caffeine.newBuilder()
            .initialCapacity(CACHE_INITIAL_CAPACITY)
            .maximumSize(Integer.MAX_VALUE)
            .expireAfterAccess(interval, TimeUnit.SECONDS)
            .build();
    }


    @Bean(name = "alarmExecutor")
    public Executor alarmExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(this.coreSize);
        executor.setMaxPoolSize(this.maxSize);
        executor.setQueueCapacity(this.capacity);
        executor.setKeepAliveSeconds(this.keepAliveSeconds);
        executor.setThreadNamePrefix("alarmExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(300);
        return executor;
    }

    @Bean
    public MonitorApiAspect apiMonitorAspect() {
        return new MonitorApiAspect();
    }
}
