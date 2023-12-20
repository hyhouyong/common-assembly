package com.kcwl.common.log.enums;

import cn.hutool.core.util.DesensitizedUtil;

public enum DesensitizedTypeEnum {
    USER_ID("USER_ID", DesensitizedUtil.DesensitizedType.USER_ID),
    CHINESE_NAME("CHINESE_NAME", DesensitizedUtil.DesensitizedType.CHINESE_NAME),
    ID_CARD("ID_CARD", DesensitizedUtil.DesensitizedType.ID_CARD),
    FIXED_PHONE("FIXED_PHONE", DesensitizedUtil.DesensitizedType.FIXED_PHONE),
    MOBILE_PHONE("MOBILE_PHONE", DesensitizedUtil.DesensitizedType.MOBILE_PHONE),
    ADDRESS("ADDRESS", DesensitizedUtil.DesensitizedType.ADDRESS),
    EMAIL("EMAIL", DesensitizedUtil.DesensitizedType.EMAIL),
    PASSWORD("PASSWORD", DesensitizedUtil.DesensitizedType.PASSWORD),
    CAR_LICENSE("CAR_LICENSE", DesensitizedUtil.DesensitizedType.CAR_LICENSE),
    BANK_CARD("BANK_CARD", DesensitizedUtil.DesensitizedType.BANK_CARD);

    public String getCode() {
        return code;
    }

    public DesensitizedUtil.DesensitizedType getDesensitizedType() {
        return desensitizedType;
    }

    private String code;
    private DesensitizedUtil.DesensitizedType desensitizedType ;

    DesensitizedTypeEnum(String code, DesensitizedUtil.DesensitizedType desensitizedType) {
        this.code = code;
        this.desensitizedType = desensitizedType;
    }

    public static DesensitizedUtil.DesensitizedType getDesensitizedType(String code) {
        code = code.toUpperCase();
        for (DesensitizedTypeEnum typeEnum : DesensitizedTypeEnum.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDesensitizedType();
            }
        }
        return null;
    }
}
