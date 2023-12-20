package com.kcwl.common.risk.enums;

import lombok.Getter;

/**
 * 短信类型枚举
 * @author ckwl
 */
@Getter
public enum SendTypeEnum {

    NOT_COMMON_DEVICE(103, "非常用设备登录预警短信"),
    NOT_COMMON_PLACE(102, "异地登录预警短信");

    private int sendType;
    private String desc;

    SendTypeEnum(int sendType, String desc) {
        this.sendType = sendType;
        this.desc = desc;
    }

}
