package com.kcwl.common.risk.enums;

import lombok.Getter;

/**
 * 风险启用状态枚举
 * @author ckwl
 */
@Getter
public enum StatusEnum {
    ENABLE(1, "启用"),
    DISABLE(0, "未启用");

    private int code;

    private String msg;

    StatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return msg;
    }
}
