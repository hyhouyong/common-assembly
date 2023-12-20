package com.kcwl.common.vo;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ckwl
 */
@Data
public class TransTypeInfoVo {
    private String desc;
    private ConcurrentHashMap<String, TrConsumeType> consumeType;
    private ConcurrentHashMap<String, TrAccountType> accountType;
    private ConcurrentHashMap<String, TrAmountFlow> amountFlow;

    public String getTransDescByConsumeType(String type) {
        TrConsumeType trConsumeType = null;
        if ( consumeType != null ) {
            trConsumeType = consumeType.get(type);
        }
        return (trConsumeType != null) ? trConsumeType.getDesc() : "";
    }

    public String getTransDescByAccountType(String type) {
        TrAccountType trAccountType = null;
        if ( accountType != null ) {
            trAccountType = accountType.get(type);
        }
        return (trAccountType != null) ? trAccountType.getDesc() : "";
    }

    public String getTransDescByAmountFlow(String flow, Integer phase) {
        TrAmountFlow trAmountFlow = null;
        if ( (amountFlow != null) && (flow != null) ) {
            trAmountFlow = amountFlow.get(flow);
        }
        return (trAmountFlow != null) ? trAmountFlow.getAmountFlowDesc(phase) : desc;
    }

    @Data
    public static class TrAmountFlow{
        private String desc="";
        private ConcurrentHashMap<String, TrPayPhase> phase;

        public String getAmountFlowDesc(Integer p) {
            TrPayPhase trPayPhase = null;
            if ( phase != null ) {
                if (p != null) {
                    trPayPhase = phase.get(p.toString());
                } else {
                    trPayPhase = phase.get("0");
                }
            }
            return (trPayPhase!=null) ? trPayPhase.getDesc() : desc;
        }

        @Data
        public static class TrPayPhase{
            private String desc;
        }
    }

    @Data
    public static class TrConsumeType{
        private String desc;
    }

    @Data
    public static class TrAccountType{
        private String desc;
    }
}
