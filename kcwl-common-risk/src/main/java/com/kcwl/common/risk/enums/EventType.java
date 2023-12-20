package com.kcwl.common.risk.enums;

/**
 * 事件类型枚举
 * @author ckwl
 */
public enum EventType {

    EVENT_TYPE_PWD_LOGIN("passwordLogin", "密码登录"),
    EVENT_TYPE_ALL_LOGIN("login", "所有登录"),
    EVENT_TYPE_SET_PASSWORD("setPassword","设置密码 "),
    EVENT_TYPE_CHECK_PASSWORD("checkPassword","检查密码");

    private String type;
    private String desc;

    EventType(String type, String desc){
        this.type=type;
        this.desc=desc;
    }

    public String getType(){
        return this.type;
    }

    public String getDesc(){
        return this.desc;
    }

    public boolean sameValueAs(String type) {
        return this.type.equals(type);
    }

}
