package com.kcwl.common.risk.enums;

/**
 * 用户被锁定枚举
 * @author ckwl
 */
public enum UserLockEnum {

    UNLOCK(0, "正常"),
    LOCK(1, "锁定");

    private Integer code;

    private String msg;

    UserLockEnum(Integer code, String msg) {
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
