package com.kcwl.common.log.enums;

/**
 * 重试状态
 */
public enum RetryStatus {

    /**
     * 仅记录，不重试
     */
    LOG_ONLY,

    /**
     * 处理中
     */
    PROCESSING,

    /**
     * 重试成功
     */
    SUCCEED,

    /**
     * 重试失败
     */
    FAILED;

}
