package com.kcwl.common.risk.enums;

/**
 * 风险名字枚举
 * @author ckwl
 */
public enum RiskName {
    RISK_OVER_PASSWORD("overPasswordAttemptRisk", "连续密码错误次数过多"),
    RISK_CHANGE_DEVICE("changeDeviceRisk", "更换设备登录"),
    RISK_CHANGE_REGION("changeRegionRisk","异地登录"),
    RISK_DEVICE_OVER_MOBILE("deviceOverMobileAttemptRisk", "同一个设备连续用不存在的手机号尝试登录"),
    RISK_DEVICE_NETWORK_MOBILE("deviceNetworkMobileAttemptRisk", "同一网络连续用不存在的手机号连续尝试"),
    RISK_OVER_DEVICE_ATTEMPT("overDeviceAttemptRisk","登录设备数过多"),
    RISK_SIMPLE_PASSWORD("simplePasswordRisk", "密码正则校验"),
    RISK_WEEK_PASSWORD("weakPasswordRisk", "弱密码"),
    RISK_USED_PASSWORD("usedPasswordRisk","最近一段使用过的密码");

    private String name;
    private String desc;

    RiskName(String name, String desc){
        this.name=name;
        this.desc=desc;
    }

    public String getName(){
        return this.name;
    }

    public String getDesc(){
        return this.desc;
    }
}
