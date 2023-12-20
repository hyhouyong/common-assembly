package com.kcwl.sensitiveword.provider;

import cn.hutool.dfa.FoundWord;
import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;

import java.util.List;

/**
 * <p>
 * 敏感词过滤服务
 * </p>
 *
 * @author renyp
 * @since 2023/5/25 17:38
 */
public interface SensitiveWordScanProvider {


    /**
     * 是否存在敏感词
     *
     * @param text 母串文本
     * @return 是否存在敏感词
     */
    boolean existsSensitiveWord(String text);

    /**
     * 是否存在敏感词
     *
     * @param text               母串文本
     * @param sensitiveWordLevel 敏感级别
     * @return 是否存在敏感词
     */
    boolean existsSensitiveWord(String text, SensitiveLevelEnum sensitiveWordLevel);


    /**
     * 查找母串文本中出现的敏感词
     *
     * @param text 母串文本
     * @return 命中的敏感词集合
     */
    FoundWord searchSensitiveWord(String text);


    /**
     * 查找母串文本中出现的敏感词
     *
     * @param text 查找的文本（母串）
     * @return 命中的敏感词集合
     */
    List<FoundWord> searchAllSensitiveWord(String text);

    /**
     * 查找母串文本中出现的敏感词
     *
     * @param text           查找的文本（母串）
     * @param isDensityMatch 是否密集匹配
     * @return 命中的敏感词集合
     */
    List<FoundWord> searchAllSensitiveWord(String text, boolean isDensityMatch);


}
