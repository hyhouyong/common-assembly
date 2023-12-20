package com.kcwl.common.risk.enums;

import lombok.Getter;

/**
 * 风险策略异常码
 *
 * @author kcwl
 */
@Getter
public enum UserRiskErrorEnum {

    USER_PASSWORD_ERROR("101", "密码已连续输入错误"),
    USER_PASSWORD_ERROR_OVER_TEN("102", "密码连续输入错误超过10次，请稍后再试"),
    NOT_COMMON_DEVICE("103", "当前登录设备不是你的常用设备，为了确保账号安全，请使用验证码方式登录"),
    NOT_COMMON_PLACE("104", "当前账号属于异地登录，为了确保账号安全，请使用验证码方式登录"),
    USED_PASSWORD("105", "新密码不能使用最近半年内使用过的密码，请重新设置"),
    NOT_FOUND_PASSWORD("106", "根据国家政策要求，需要设置登录密码后才能操作系统"),
    WEAK_PASSWORD("107", "根据国家政策要求，你的密码存在安全风险，请先修改密码"),
    FORCE_PASSWORD_LOGIN("108", "根据国家政策要求，你的账号可能存在安全风险，请使用登录密码方式登录");


    private String code;
    private String msg;
    UserRiskErrorEnum(String code, String msg) {
        this.code = code;
        this.msg  = msg;
    }

}
