package com.kcwl.common.tool;

/**
 * @author ckwl
 */
public enum CycleFormatEnum {

    /**
     * 日期格式
     */
    YY_MM_DD_HH_MM_SS("yyMMddHHmmss", "yyMMddHHmm", 60+10),
    YY_MM_DD_HH_MM("yyMMddHHmm", "yyMMddHHmm", 60+60),
    YY_MM_DD_HH("yyMMddHH", "yyMMddHH", 60*60+60),
    YY_MM_DD("yyMMdd", "yyMMdd", 24*60*60+60);

    private String format;
    private String key;
    private int life;

    CycleFormatEnum(String format, String key, int life) {
        this.format = format;
        this.key=key;
        this.life = life;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
