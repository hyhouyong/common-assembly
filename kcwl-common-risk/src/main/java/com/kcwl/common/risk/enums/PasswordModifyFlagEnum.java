package com.kcwl.common.risk.enums;

/**
 * @author Administrator
 */
public enum PasswordModifyFlagEnum {

    PWD_NOT_MODIFIED(0, "6个月内没有修改过密码"),
    PWD_MODIFIED(1, "6个月内修改过密码");

    private int code;
    private String msg;

    PasswordModifyFlagEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

}
