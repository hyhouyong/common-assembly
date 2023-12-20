package com.kcwl.sensitiveword.enums;

import lombok.Getter;

import java.util.Optional;

/**
 * <p>
 * 敏感级别枚举
 * </p>
 *
 * @author renyp
 * @since 2023/5/25 17:39
 */

@Getter
public enum SensitiveLevelEnum {

    /**
     * weak(,100)
     */
    WEAK("WEAK", 1 << 2),
    /**
     * normal(,010)
     */
    NORMAL("NORMAL", 1 << 1),
    /**
     * strict(,001)
     */
    STRICT("STRICT", 1);

    private final String code;
    private final int bitShift;

    SensitiveLevelEnum(String code, int bitShift) {
        this.code = code;
        this.bitShift = bitShift;
    }

    public static Optional<SensitiveLevelEnum> exchangeByLevelValue(String value) {
        for (SensitiveLevelEnum sensitiveLevelEnum : SensitiveLevelEnum.values()) {
            if (sensitiveLevelEnum.code.equals(value)) {
                return Optional.of(sensitiveLevelEnum);
            }
        }
        return Optional.empty();
    }


}
