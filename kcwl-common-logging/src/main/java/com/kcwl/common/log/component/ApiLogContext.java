package com.kcwl.common.log.component;

import com.kcwl.common.log.enums.RetryWaitStrategies;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.function.Supplier;

/**
 * 异常日志上下文 执行环境
 */
public final class ApiLogContext {

    // 不要手动瞎操作这个
    public final static ThreadLocal<ContextConfig> context = new InheritableThreadLocal<>();

    private ApiLogContext() {
    }

    // 创建上下文环境，使用默认配置
    public static ContextConfig create() {
        return new ContextConfig();
    }

    // 仅记录（不重试）
    public static ContextConfig logOnly() {
        return new LogOnlyContextConfig();
    }

    /**
     * 仅记录
     * @param bizCode 业务标识（无功能作用，仅作区分）
     */
    public static ContextConfig logOnly(String bizCode) {
        return new LogOnlyContextConfig().setBizCode(bizCode);
    }

    // 重试
    public static ContextConfig retry() {
        return new ContextConfig();
    }

    /**
     * 重试
     * @param bizCode 业务标识（无功能作用，仅作区分）
     */
    public static ContextConfig retry(String bizCode) {
        return new ContextConfig().setBizCode(bizCode);
    }

    /**
     * 异常日志上下文 执行环境 配置
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextConfig {

        // 是否开启重试，默认开启（不开启时，报错时仅记录）
        private Boolean retryEnabled = true;

        // 重试次数（当前接口请求失败不计入错误内）
        private Integer retryTimes = 3;

        // 重试等待间隔
        private Integer retryWaitInterval = 60;

        // 重试等待间隔策略
        private RetryWaitStrategies retryWaitStrategy = RetryWaitStrategies.FIXED;

        // 业务标识（无功能作用，仅作区分）
        private String bizCode;

        // 抛异常时，不记录
        private Boolean ignoreIfException = false;

        // 执行
        public ContextConfig execute(Runnable runnable) {
            try {
                context.set(this);
                runnable.run();
            } finally {
                context.remove();
            }
            return this;
        }

        // 执行并返回值
        public <T> T executeAndReturn(Supplier<T> supplier) {
            try {
                context.set(this);
                return supplier.get();
            } finally {
                context.remove();
            }
        }

    }

    /**
     * 只读配置
     */
    public static class LogOnlyContextConfig extends ContextConfig {

        protected LogOnlyContextConfig() {
        }

        @Override
        public Boolean getRetryEnabled() {
            return false;
        }

    }

}
