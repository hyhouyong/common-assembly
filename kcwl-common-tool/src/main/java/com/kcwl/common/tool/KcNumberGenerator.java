package com.kcwl.common.tool;

import com.kcwl.ddd.infrastructure.api.CommonCode;
import com.kcwl.ddd.infrastructure.exception.BizException;
import com.kcwl.framework.cache.ICacheService;
import com.kcwl.framework.utils.DecimalConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 业务编号生成工具类
 *  位数：总共22位
 *  规则：业务类型（2位）+平台码（3位）+业务规则（最大2位）+时间戳（12位）+每分钟内的序号（3到5位）
 * @author ckwl
 */
@Slf4j
@Component
public class KcNumberGenerator {

    private static final int MAX_BUFFER_LEN =32;
    private static final int MAX_CODE_LEN =5;
    private static final int MAX_RANDOM_SUFFIX_LEN = 3;

    @Resource
    ICacheService stringCache;

    /**
     * 生成业务单编号
     * @param platformNo 平台码
     * @param bizType 业务类型，为2位固定字母，大小写随意
     * @param bizRuleCode 业务规则码，不需要传规则传空字符串；最大长度为2位；
     * @return
     */
    public String generate(String platformNo, String bizType, String bizRuleCode) {
        assertBizType(bizType);
        assertBizRuleCode(bizRuleCode);
        int codeLen = MAX_CODE_LEN - bizRuleCode.length();
        return generateBizNo(platformNo, bizType, bizRuleCode, CycleFormatEnum.YY_MM_DD_HH_MM_SS, codeLen);
    }

    /**
     * @param platformNo 平台码
     * @param bizType 业务类型，为2位固定字母，大小写随意
     * @param bizRuleCode 业务规则编码
     * @param cycleFormat 随机码的循环周期格式
     * @return 返回业务编码
     */
    public String generate(String platformNo, String bizType, String bizRuleCode, CycleFormatEnum cycleFormat) {
        assertBizType(bizType);
        return generateBizNo(platformNo, bizType, bizRuleCode, cycleFormat,MAX_RANDOM_SUFFIX_LEN);
    }

    /**
     * @param platformNo 平台码
     * @param bizType 业务类型
     * @param bizRuleCode 业务规则编码
     * @param cycleFormat 随机码的循环周期格式
     * @param cycleLen  随机码的长度
     * @return 返回业务编码
     */
    public String generateBizNo(String platformNo, String bizType, String bizRuleCode, CycleFormatEnum cycleFormat, int cycleLen) {
        Date currentDate = new Date();
        StringBuilder sb = new StringBuilder(MAX_BUFFER_LEN);
        String cycleKey = DateFormatUtils.format(currentDate, cycleFormat.getKey());
        String cacheKey = getCacheKey(bizType, platformNo, cycleKey);
        int codeSeq = stringCache.incrementAndGet(cacheKey, cycleFormat.getLife());
        sb.append(bizType);
        sb.append(platformNo);
        sb.append(bizRuleCode);
        sb.append(DateFormatUtils.format(currentDate, cycleFormat.getFormat()));
        sb.append(DecimalConvert.longToBase62(codeSeq, cycleLen));
        return sb.toString();
    }

    private String getCacheKey(String bizType, String platformNo, String cycleKey) {
        StringBuilder sb = new StringBuilder(MAX_CODE_LEN);
        sb.append("ng:code:").append(bizType).append(":").append(platformNo).append(":").append(cycleKey);
        return sb.toString();
    }

    private void assertBizType(String bizType) {
        if ( (bizType == null) || bizType.length() != 2 ) {
            log.error("业务类型不正确：{}", bizType);
            throw new BizException(CommonCode.DATA_LOGIC_ERROR_CODE.getCode(), "业务类型不正确！");
        }
    }

    private void assertBizRuleCode(String bizRuleCode) {
        if ( (bizRuleCode == null) || bizRuleCode.length() > 2 ) {
            log.error("业务规则码不正确：{}", bizRuleCode);
            throw new BizException(CommonCode.DATA_LOGIC_ERROR_CODE.getCode(), "业务规则码不正确！");
        }
    }
}
