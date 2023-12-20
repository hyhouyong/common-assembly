package com.kcwl.common.log.enums;

/**
 * 重试等待间隔策略
 */
public enum RetryWaitStrategies {

    /**
     * 定长等待
     * 60s、60s、60s、60s
     */
    FIXED,

    /**
     * 增量等待
     * 60s、120s、180s、240s
     */
    INCREMENT,

    /**
     * 斐波那契等待
     * 60s、60s、120s、180s、300s ....
     */
    FIBONACCI;

    /**
     * 下次重试时间
     *
     * @param lastRetryTime     上一次重试时间
     * @param retryCount        已重试次数
     * @param retryWaitInterval 重试间隔
     */
    public long nextRetryTime(long lastRetryTime, int retryCount, int retryWaitInterval) {
        switch (this) {
            case FIXED: {
                return lastRetryTime + retryWaitInterval;
            }
            case INCREMENT: {
                return lastRetryTime + (long) retryWaitInterval * (retryCount + 1);
            }
            case FIBONACCI: {
                return lastRetryTime + fibs(retryCount + 1, retryWaitInterval);
            }
            default:
                throw new RuntimeException("不支持的枚举");
        }

    }


    /**
     * 斐波那契实现
     * 60s、60s、120s、180s、300s ....
     *
     * @param index 从 1 开始
     * @param val   值
     */
    private static int fibs(int index, int val) {

        if (index <= 0) {
            throw new RuntimeException();
        }

        int slow = val, fast = val;
        for (int i = 2; i < index; i++) {
            int fastCarry = fast;
            fast += slow;
            slow = fastCarry;
        }

        return fast;
    }

}
