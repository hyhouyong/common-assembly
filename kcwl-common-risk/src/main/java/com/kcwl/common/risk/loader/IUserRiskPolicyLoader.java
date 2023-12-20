package com.kcwl.common.risk.loader;

import com.kcwl.common.risk.entity.UserRiskPolicy;

import java.util.List;

/**
 * 风险策略加载器
 * @author ckwl
 */
public interface IUserRiskPolicyLoader {
    /**
     * 获取当前配置的用户风险策略
     * @param eventType 事件类型
     * @return 返回风险策略列表
     */
    List<UserRiskPolicy> getUserRiskPolicyList(String eventType);

    /**
     * 根据风险名字获取用户风险策略
     * @param riskName 风险名字
     * @return 返回风险策略
     */
    UserRiskPolicy getUserRiskPolicy(String riskName);
}
