package com.kcwl.sensitiveword;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import com.kcwl.sensitiveword.enums.SensitiveWordTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 格式化 生成敏感词文件词库
 * </p>
 *
 * @author renyp
 * @since 2023/5/26 16:38
 */
public class FileDataSourceFormatter {

    private static Integer index = 1;


    public static void main(String[] args) {
        List<String> wordInfoList = new ArrayList<>();
        if (FileUtil.exist("temp/广告.txt")) {
            FileUtil.readUtf8Lines("temp/广告.txt").stream().filter(StrUtil::isNotBlank).forEach(word -> wordInfoList.add(sensitiveWordInfoPretty(word, SensitiveWordTypeEnum.ADVERTISING)));
        }
        if (FileUtil.exist("temp/政治类.txt")) {
            FileUtil.readUtf8Lines("temp/政治类.txt").stream().filter(StrUtil::isNotBlank).forEach(word -> wordInfoList.add(sensitiveWordInfoPretty(word, SensitiveWordTypeEnum.POLITICS)));
        }
        if (FileUtil.exist("temp/涉枪涉爆违法信息关键词.txt")) {
            FileUtil.readUtf8Lines("temp/涉枪涉爆违法信息关键词.txt").stream().filter(StrUtil::isNotBlank).forEach(word -> wordInfoList.add(sensitiveWordInfoPretty(word, SensitiveWordTypeEnum.LAW)));
        }
        if (FileUtil.exist("temp/网址.txt")) {
            FileUtil.readUtf8Lines("temp/网址.txt").stream().filter(StrUtil::isNotBlank).forEach(word -> wordInfoList.add(sensitiveWordInfoPretty(word, SensitiveWordTypeEnum.URL)));
        }
        if (FileUtil.exist("temp/色情类.txt")) {
            FileUtil.readUtf8Lines("temp/色情类.txt").stream().filter(StrUtil::isNotBlank).forEach(word -> wordInfoList.add(sensitiveWordInfoPretty(word, SensitiveWordTypeEnum.PORNOGRAPHIC)));
        }
        FileUtil.writeLines(wordInfoList, "sensitive_word.txt", CharsetUtil.CHARSET_UTF_8, true);
    }

    private static String sensitiveWordInfoPretty(String word, SensitiveWordTypeEnum sensitiveWordType) {
        // 2023/5/29 格式化敏感词信息
        return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s", index++, word, SensitiveLevelEnum.NORMAL.getCode(), sensitiveWordType.getCode(), "1", DateUtil.now(), "system init", DateUtil.now(), "system init");
    }

}