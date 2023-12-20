package com.kcwl.common.risk.enums;

import lombok.Getter;

/**
 * 用户黑名单类型枚举
 * @author ckwl
 */
@Getter
public enum BlacklistTypeEnum {

    TYPE_DEVICE(1, "设备"),
    TYPE_IP(2, "ip");

    private Integer code;
    private String msg;
    BlacklistTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode() {
        return code;
    }
    public String getDesc() {
        return msg;
    }

}
