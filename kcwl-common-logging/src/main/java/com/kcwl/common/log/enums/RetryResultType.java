package com.kcwl.common.log.enums;

/**
 * 重试状态
 */
public enum RetryResultType {

    /**
     * 运行中
     */
    RUNNING,

    /**
     * 成功
     */
    SUCCESS,

    /**
     * 执行失败
     */
    RUN_FAILED,

    /**
     * 主表数据丢失
     */
    MISSING_LOG,

}
