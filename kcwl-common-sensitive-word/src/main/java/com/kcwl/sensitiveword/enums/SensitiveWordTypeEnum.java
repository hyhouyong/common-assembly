package com.kcwl.sensitiveword.enums;

import lombok.Getter;

import java.util.Optional;

/**
 * <p>
 * 敏感词类型枚举
 * </p>
 *
 * @author renyp
 * @since 2023/5/25 17:40
 */
@Getter
public enum SensitiveWordTypeEnum {

    /**
     * 全部类型
     */
    ALL("ALL"),
    /**
     * 政治
     */
    POLITICS("POLITICS"),
    /**
     * 法律
     */
    LAW("LAW"),
    /**
     * 广告
     */
    ADVERTISING("ADVERTISING"),
    /**
     * 网址
     */
    URL("URL"),
    /**
     * 色情
     */
    PORNOGRAPHIC("PORNOGRAPHIC"),
    /**
     * 其他
     */
    OTHER("OTHER");

    private final String code;

    SensitiveWordTypeEnum(String code) {
        this.code = code;
    }

    public static Optional<SensitiveWordTypeEnum> exchangeByLevelValue(String value) {
        for (SensitiveWordTypeEnum sensitiveWordTypeEnum : SensitiveWordTypeEnum.values()) {
            if (sensitiveWordTypeEnum.code.equals(value)) {
                return Optional.of(sensitiveWordTypeEnum);
            }
        }
        return Optional.empty();
    }

}
