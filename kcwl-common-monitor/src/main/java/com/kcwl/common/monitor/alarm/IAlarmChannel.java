package com.kcwl.common.monitor.alarm;

/**
 * @author ckwl
 */
public interface IAlarmChannel {
    /**
     * 给用户发送通知消息
     * @param toUser 告警用户
     * @param message 告警消息
     */
    void alarm(String toUser, String message);
}
