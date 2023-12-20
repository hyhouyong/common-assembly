package com.kcwl.common.monitor.alarm.impl;

import com.kcwl.common.monitor.alarm.IAlarmChannel;
import com.kcwl.common.monitor.properties.DingTalkProperties;
import com.kcwl.framework.utils.KcDingTalkRobotUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ckwl
 */
@Component
public class DingTalkAlarmChannel implements IAlarmChannel {

    @Resource
    DingTalkProperties dingTalkProperties;

    /**
     * 给用户发送通知消息
     *
     * @param toUser  告警用户
     * @param message 告警消息
     */
    @Override
    public void alarm(String toUser, String message) {
        if ( dingTalkProperties.isEnabled() ) {
            KcDingTalkRobotUtil.sendText(dingTalkProperties.getWebhook(), dingTalkProperties.getSecret(), message);
        }
    }
}
