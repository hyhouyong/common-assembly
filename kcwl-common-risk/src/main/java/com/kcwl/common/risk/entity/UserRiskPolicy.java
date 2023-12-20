package com.kcwl.common.risk.entity;

import lombok.Data;

import java.util.Date;

/**
 * 风险策略数据库实体
 * @author ckwl
 */
@Data
public class UserRiskPolicy implements Comparable<UserRiskPolicy>{
    private Long id;

    /**
     * 风险名字
     */
    private String riskName;

    /**
     * 风险因子/参数
     */
    private String riskFactor;

    /**
     * 风险详情
     */
    private String riskDesc;

    /**
     * 事件类型：登录、注册、设置密码……
     */
    private String eventType;

    /**
     * 风险检查点：0事件发生前；1事件进行中；2事件发生后
     */
    private Integer checkPoint;

    /**
     * 检查顺序
     */
    private Integer checkSort;

    /**
     * 处置措施
     */
    private String riskAction;

    /**
     * 触发等级
     */
    private Integer actionLevel;

    /**
     * 处置参数
     */
    private String actionParam;

    /**
     * 处置描述
     */
    private String actionDesc;

    /**
     * 状态：0未启用；1启用
     */
    private Integer policyStatus;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    @Override
    public int compareTo(UserRiskPolicy o) {
        if(this.getCheckSort() == null || o == null || o.getCheckSort() == null){
            return 0;
        }
        return this.getCheckSort().compareTo(o.getCheckSort());
    }

}
