package com.kcwl.common.risk.strategy.risk.impl;

import com.kcwl.common.risk.constant.PolicyConstant;
import com.kcwl.common.risk.entity.UserRiskPolicyArgs;
import com.kcwl.common.risk.enums.RiskLevel;
import com.kcwl.common.risk.strategy.risk.IUserRisk;
import com.kcwl.common.risk.util.RedisKeyUtil;
import com.kcwl.framework.cache.ICacheService;
import com.kcwl.framework.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * "同一个设备连续用不存在的手机号尝试登录"风险识别
 * @author ckwl
 */
@Component
@Slf4j
public class DeviceOverMobileAttemptRisk implements IUserRisk {

    @Autowired
    protected ICacheService stringCache;

    @Override
    public RiskLevel check(Object params, String factor) {
        UserRiskPolicyArgs userRiskPolicyArgs = (UserRiskPolicyArgs)params;

        // 如果factor是空，初始化默认值
        if(StringUtil.isBlank(factor)){
            factor = PolicyConstant.TEN;
        }

        // 本次登录的终端
        String currentKcToken = userRiskPolicyArgs.getKcToken();

        // 获取不到本次登录的终端信息
        if(StringUtils.isBlank(currentKcToken)){
            return RiskLevel.RISK_NONE;
        }

        Object count = stringCache.get(RedisKeyUtil.getLoginKcTokenUserNotExistCount(currentKcToken));
        if (count != null && Integer.parseInt(count.toString()) >= Integer.parseInt(factor)) {
            return RiskLevel.RISK_HIGH;
        } else {
            return RiskLevel.RISK_NONE;
        }
    }

}
