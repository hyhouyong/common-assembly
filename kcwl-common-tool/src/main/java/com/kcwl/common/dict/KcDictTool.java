package com.kcwl.common.dict;

import com.kcwl.common.vo.BankInfoVo;
import com.kcwl.common.vo.PayChanelVo;
import com.kcwl.common.vo.PlatformInfoVo;
import com.kcwl.common.vo.UserTagInfoVo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ckwl
 */
@Component
public class KcDictTool {

    @Resource
    KcDictProperties kcDictProperties;

    final PlatformInfoVo emptyPlatformInfo = new PlatformInfoVo();

    /**
     *  获取交易类型描述
     *
     * @param transactionType  交易类型
     * @param flow 资金流入，流出标识
     * @param phase 支付阶段
     * @return  返回交易类型描述信息
     */
    public String getTransTypeDesc(Integer transactionType, Integer consumeType, Integer  accountType, String flow, Integer phase) {
        String transType = String.valueOf(transactionType);
        if (  consumeType != null ) {
             return kcDictProperties.getTransTypeDesc(transType, consumeType.toString(), null, null, null);
        }
        if (  accountType != null ) {
            return kcDictProperties.getTransTypeDesc(transType, null, accountType.toString(), null, null);
        }
        return kcDictProperties.getTransTypeDesc(transType, null, null, flow, phase);
    }

    /**
     * 获取银行名称
     * @param bankCode  银行code
     * @return 银行名称
     */
    public String getBankName(String bankCode) {
        BankInfoVo bankInfoVo = kcDictProperties.getBankInfo(bankCode);
        return (bankInfoVo != null ) ? bankInfoVo.getName() : bankCode;
    }

    /**
     * 获取交易渠道名称
     * @param channel
     * @return
     */
    public String getPayChannelName(Integer channel) {
        PayChanelVo payChanelVo = null;
        if (channel != null)  {
            payChanelVo = kcDictProperties.getPayChannel(channel.toString());
        }
        return (payChanelVo != null) ? payChanelVo.getName() : null;
    }

    /**
     * 获取平台信息
     * @param platformNo 平台码
     * @return  平台信息
     */
    public PlatformInfoVo getPlatformInfo(String platformNo) {
        PlatformInfoVo platformInfoVo = kcDictProperties.getPlatformInfo(platformNo);
        if ( platformInfoVo != null ) {
            platformInfoVo.setPlatformNo(platformNo);
        }
        return (platformInfoVo!=null) ? platformInfoVo : emptyPlatformInfo;
    }

    public List<PlatformInfoVo> getPlatformList() {
        List<PlatformInfoVo> platformInfoVoList = new ArrayList<PlatformInfoVo>();
        if ( kcDictProperties.getPlatformMap() != null ) {
            kcDictProperties.getPlatformMap().forEach((k,v)->{
                if (StringUtils.isEmpty(v.getPlatformNo()) ) {
                    v.setPlatformNo(k);
                }
                platformInfoVoList.add(v);
            });
        }
        return platformInfoVoList;
    }

    public BankInfoVo getBankInfo(String bankCode) {
        return kcDictProperties.getBankInfo(bankCode);
    }

    public UserTagInfoVo getUserTagInfo(Byte userTag){
        return kcDictProperties.getUserTagInfo(userTag);
    }

    /**
     * 根据userTag获取对应平台码列表
     * @param userTag 用户标识
     * @return 如果配置了映射关系，返回用户选择的平台码列表；否则返回null
     */
    public List<String> getSelectPlatformList(Byte userTag) {
        UserTagInfoVo userTagInfoVo = kcDictProperties.getUserTagInfo(userTag);
        return (userTagInfoVo != null ) ? userTagInfoVo.getSelectPlatformList() : null;
    }

    /**
     * 根据userTag获取不需要查看的平台码列表
     * @param userTag 用户标识
     * @return 如果配置了映射关系，返回需要排除的平台码列表；否则返回null
     */
    public List<String> getDiscardPlatformList(Byte userTag) {
        UserTagInfoVo userTagInfoVo = kcDictProperties.getUserTagInfo(userTag);
        return (userTagInfoVo != null ) ? userTagInfoVo.getDiscardPlatformList() : null;
    }
}
