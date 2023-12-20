package com.kcwl.common.monitor.enums;

/**
 * @author ckwl
 */

public enum AlarmMode {

    /**
     * 告警模式
     */
    ALARM_NONE(0, "不告警"),
    ALARM_ALL(1, "告警所有异常"),
    ALARM_FATAL(2, "所有异常");

    private int mode;
    private String desc;

    AlarmMode(int mode, String desc) {
        this.mode = mode;
        this.desc = desc;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
