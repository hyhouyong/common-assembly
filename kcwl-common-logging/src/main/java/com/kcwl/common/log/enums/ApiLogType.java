package com.kcwl.common.log.enums;

/**
 * 接口日志记录类型
 */
public enum ApiLogType {

    /**
     * 被调用接口异常
     * （被请求）
     */
    FAIL,

    /**
     * 手动记录
     * （主动向外请求）
     */
    MANUAL_LOG,

    /**
     * 手动记录并重试
     * （主动向外请求）
     */
    MANUAL_RETRY;

}
