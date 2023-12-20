package com.kcwl.common.risk.strategy.risk;

import com.kcwl.common.risk.enums.RiskLevel;

/**
 * 风险识别
 * @author ckwl
 */
public interface IUserRisk {
    /**
     * 检测当前用户行为的风险
     * @param params 业务参数
     * @param factor 风险因子/风险参数
     * @return 返回风险等级
     */
    RiskLevel check(Object params, String factor);
}
