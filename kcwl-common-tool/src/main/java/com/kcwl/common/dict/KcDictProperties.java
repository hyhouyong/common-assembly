package com.kcwl.common.dict;

import com.kcwl.common.vo.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ckwl
 */
@Data
@Component
@ConfigurationProperties(prefix = "kcwl.dict")
public class KcDictProperties {

    ConcurrentHashMap<String, PlatformInfoVo> platformMap;

    ConcurrentHashMap<String, BankInfoVo> bankMap;

    ConcurrentHashMap<String, TransTypeInfoVo> transMap;

    ConcurrentHashMap<String, PayChanelVo> payChannelMap;

    ConcurrentHashMap<Byte, UserTagInfoVo> userTagMap;

    public PlatformInfoVo getPlatformInfo(String platformNo) {
        return (platformMap !=null) ? platformMap.get(platformNo) : null;
    }

    public BankInfoVo getBankInfo(String bankCode) {
        return (bankMap != null) ? bankMap.get(bankCode) : null;
    }

    public PayChanelVo getPayChannel(String channel) {
        return (payChannelMap != null) ? payChannelMap.get(channel) : null;
    }

    public String getTransTypeDesc(String transType, String consumeType, String accountType, String flow, Integer phase) {
        String transTypeDesc = "";
        if ( transMap != null  ) {
            TransTypeInfoVo transTypeInfoVo = transMap.get(transType);
            if ( transTypeInfoVo != null ) {
                if (  consumeType != null ) {
                    transTypeDesc = transTypeInfoVo.getTransDescByConsumeType(consumeType);
                } else if ( accountType != null ) {
                    transTypeDesc = transTypeInfoVo.getTransDescByAccountType(accountType);
                } else {
                    transTypeDesc = transTypeInfoVo.getTransDescByAmountFlow(flow, phase);
                }
            }
        }
        return transTypeDesc;
    }

    public UserTagInfoVo getUserTagInfo(Byte userTag){
        return ( (userTagMap != null) && (userTag!=null) ) ? userTagMap.get(userTag) : null;
    }
}
