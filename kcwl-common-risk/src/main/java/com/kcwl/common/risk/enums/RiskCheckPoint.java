package com.kcwl.common.risk.enums;

/**
 * 风险检查点枚举
 * @author ckwl
 */
public enum RiskCheckPoint {
    /**
     * 定义风险检查点
     */
    EVENT_BEFORE(0, "事件发生前"),
    EVENT_IN(1, "事件进行中"),
    EVENT_AFTER(2,"事件发生后 ");

    private Integer point;
    private String desc;

    RiskCheckPoint(int point, String desc){
        this.point=point;
        this.desc=desc;
    }

    public Integer getPoint(){
        return this.point;
    }

    public String getDesc(){
        return this.desc;
    }

    public boolean sameValueAs(int point) {
        return this.point.equals(point);
    }

    public static RiskCheckPoint getProductEnum(int point) {
        for (RiskCheckPoint riskCheckPoint : values()) {
            if (riskCheckPoint.getPoint().equals(point)) {
                return riskCheckPoint;
            }
        }
        return null;
    }
}
