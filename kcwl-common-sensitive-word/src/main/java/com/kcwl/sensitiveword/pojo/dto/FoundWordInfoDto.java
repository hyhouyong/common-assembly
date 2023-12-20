package com.kcwl.sensitiveword.pojo.dto;

import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import com.kcwl.sensitiveword.enums.SensitiveWordTypeEnum;

/**
 * <p>
 * 敏感词 匹配结果
 * </p>
 *
 * @author renyp
 * @since 2023/5/29 10:29
 */
public class FoundWordInfoDto {

    public FoundWordInfoDto(String foundWord, long startIndex, long endIndex, SensitiveLevelEnum sensitiveWordLevel, SensitiveWordTypeEnum sensitiveWordType) {
        this.foundWord = foundWord;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.sensitiveWordLevel = sensitiveWordLevel;
        this.sensitiveWordType = sensitiveWordType;
    }

    private final String foundWord;
    private final Long startIndex;
    private final Long endIndex;
    private final SensitiveLevelEnum sensitiveWordLevel;
    private final SensitiveWordTypeEnum sensitiveWordType;


    public String getFoundWord() {
        return foundWord;
    }

    public Long getStartIndex() {
        return startIndex;
    }

    public Long getEndIndex() {
        return endIndex;
    }

    public SensitiveLevelEnum getSensitiveWordLevel() {
        return sensitiveWordLevel;
    }

    public SensitiveWordTypeEnum getSensitiveWordType() {
        return sensitiveWordType;
    }


}
