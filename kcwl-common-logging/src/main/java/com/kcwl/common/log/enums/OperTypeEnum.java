package com.kcwl.common.log.enums;

public enum OperTypeEnum {
    DEFAULT("0", "未定义"),
    ADD("1", "新增"),
    EDIT("2", "编辑"),
    VIEW("3", "查看"),
    QUERY("4", "搜索"),
    DELETE("5", "删除"),
    EXPORT("6", "导出"),
    LOGIN_OUT("7", "登录/登出"),
    AUDIT("8", "审核"),
    MODIFY_PWD("9", "修改密码");

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private String code;
    private String msg;

    OperTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
