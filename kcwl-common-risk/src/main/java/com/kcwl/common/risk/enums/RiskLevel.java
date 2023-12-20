package com.kcwl.common.risk.enums;

/**
 * 风险等级枚举
 * @author ckwl
 */
public enum RiskLevel{

    /**
     * 定义风险等级
     */
    RISK_NONE(0, "无风险"),
    RISK_NORMAL(50, "普通风险"),
    RISK_HIGH(100,"高风险 ");

    private Integer level;
    private String desc;

    RiskLevel(int level, String desc){
        this.level=level;
        this.desc=desc;
    }

    public Integer getLevel(){
        return this.level;
    }

    public boolean greaterThan(Integer riskLevel){
        return this.level.compareTo(riskLevel)>=0;
    }

    public String getDesc(){
        return this.desc;
    }
}
