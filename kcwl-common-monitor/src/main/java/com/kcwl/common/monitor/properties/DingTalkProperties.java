package com.kcwl.common.monitor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ckwl
 */
@Data
@Component
@ConfigurationProperties(prefix = "kcwl.alarm.ding-talk")
public class DingTalkProperties {
    private boolean enabled = false;
    /**
     * 回调地址
     */
    private String webhook;
    /**
     * 密钥（签名用）
     */
    private String secret;

}
