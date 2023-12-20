package com.kcwl.common.risk.entity;

import lombok.Data;

/**
 * 风险策略执行方法传参实体
 * @author ckwl
 */
@Data
public class UserRiskPolicyArgs {

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 密码
     */
    private String password;


    /**
     * kcToken
     */
    private String kcToken;

    /**
     * ip
     */
    private String loginIpAddress;

    /**
     * 是否发送预警短信 true 发送， false 不发送
     */
    private Boolean sendWarningNoticeFlag;

}
