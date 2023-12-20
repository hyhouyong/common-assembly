package com.kcwl.sensitiveword.datasource;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.kcwl.ddd.interfaces.dto.PageInfoDTO;
import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import com.kcwl.sensitiveword.enums.SensitiveWordTypeEnum;
import com.kcwl.sensitiveword.pojo.command.SensitiveWordInfoCommand;
import com.kcwl.sensitiveword.pojo.dto.SensitiveWordInfoDto;
import com.kcwl.sensitiveword.pojo.po.SensitiveWordInfoSlim;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 本地文件 敏感词 词库
 * </p>
 *
 * @author renyp
 * @since 2023/5/26 14:48
 */

@Slf4j
public class FileDataSourceAdapter implements DataSourceAdapter {

    /**
     * 敏感词本地词库 文件名
     */
    private final String SENSITIVE_WORD_FILE_NAME = "sensitive_word.txt";
    /**
     * 敏感词信息 行内 分隔符
     */
    private final String SENSITIVE_WORD_FILE_SEPARATOR = "|";
    /**
     * 敏感词信息 行间 分隔符
     */
    private final String SENSITIVE_WORD_FILE_LINE_SEPARATOR = "End";
    /**
     * 敏感词信息 字段数量
     */
    private final Integer WORD_INFO_FIELD_COUNT = 9;

    @Override
    public List<SensitiveWordInfoSlim> reloadSensitiveWord() {
        List<SensitiveWordInfoSlim> sensitiveWordRepositoryTemp = new LinkedList<>();
        try {
            String sensitiveWordInfo = StreamUtils.copyToString(new ClassPathResource(SENSITIVE_WORD_FILE_NAME).getInputStream(), CharsetUtil.CHARSET_UTF_8);
            if (StrUtil.isBlank(sensitiveWordInfo)) {
                log.warn("敏感词本地文件词库，内容为空，请确认！");
                return sensitiveWordRepositoryTemp;
            }
            for (String wordInfo : sensitiveWordInfo.split(SENSITIVE_WORD_FILE_LINE_SEPARATOR)) {
                String[] wordInfoArray = StringUtils.split(wordInfo, SENSITIVE_WORD_FILE_SEPARATOR);
                if (null == wordInfoArray || wordInfoArray.length != WORD_INFO_FIELD_COUNT ||
                        StrUtil.isBlank(wordInfoArray[1].trim()) || StrUtil.isBlank(wordInfoArray[2].trim()) || StrUtil.isBlank(wordInfoArray[3].trim())) {
                    log.warn("敏感词本地文件词库，当前行信息格式不正确！敏感词当前行：{}", wordInfo);
                    continue;
                }
                sensitiveWordRepositoryTemp.add(SensitiveWordInfoSlim.builder()
                        .sensitiveWord(wordInfoArray[1].trim())
                        .sensitiveLevel(wordInfoArray[2].trim())
                        .typeName(wordInfoArray[3].trim())
                        .build());
            }
        } catch (FileNotFoundException e) {
            log.error("读取敏感词本地文件词库，文件异常：", e);
        } catch (IOException e) {
            log.error("读取敏感词本地文件词库，IO异常：", e);
        }
        return sensitiveWordRepositoryTemp;
    }

    @Override
    public PageInfoDTO<SensitiveWordInfoDto> querySensitiveWordPage(SensitiveWordTypeEnum sensitiveWordType, SensitiveLevelEnum sensitiveWordLevel) {
        return new PageInfoDTO<>();
    }

    @Override
    public boolean saveSensitiveWordAndRefresh(SensitiveWordInfoCommand sensitiveWordInfoCommand) {
        return true;
    }

    @Override
    public boolean saveBatchSensitiveWordAndRefresh(List<SensitiveWordInfoCommand> sensitiveWordInfoCommands) {
        return true;
    }

    @Override
    public boolean removeSensitiveWordAndRefresh(Long id) {
        return true;
    }

    /**
     * 格式化 敏感词 文本
     *
     * @param index             索引
     * @param word              敏感词
     * @param sensitiveWordType 敏感词类型
     * @return 敏感词 格式化 文本
     */
    private static String sensitiveWordInfoPretty(int index, String word, SensitiveWordTypeEnum sensitiveWordType) {
        // 2023/5/29 格式化敏感词信息
        return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s", index, word, SensitiveLevelEnum.NORMAL.getCode(), sensitiveWordType.getCode(), "1", DateUtil.now(), "system init", DateUtil.now(), "system init");
    }
}
