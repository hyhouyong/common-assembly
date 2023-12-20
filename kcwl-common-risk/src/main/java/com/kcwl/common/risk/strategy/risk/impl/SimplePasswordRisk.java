package com.kcwl.common.risk.strategy.risk.impl;

import com.kcwl.common.risk.constant.PolicyConstant;
import com.kcwl.common.risk.entity.UserRiskPolicyArgs;
import com.kcwl.common.risk.enums.RiskLevel;
import com.kcwl.common.risk.strategy.risk.IUserRisk;
import com.kcwl.common.risk.util.SimplePasswordUtil;
import com.kcwl.framework.utils.KcPasswordUtil;
import com.kcwl.framework.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * "密码正则校验"风险识别
 * @author ckwl
 */
@Component
@Slf4j
public class SimplePasswordRisk implements IUserRisk {

    @Override
    public RiskLevel check(Object params, String factor) {
        UserRiskPolicyArgs userRiskPolicyArgs = (UserRiskPolicyArgs)params;

        if(StringUtil.isBlank(factor)){
            factor = PolicyConstant.RISK_FACTOR_THREE;
        }

        if(StringUtil.isBlank(userRiskPolicyArgs.getPassword())){
            return RiskLevel.RISK_NONE;
        }
        if (!KcPasswordUtil.checkPasswordRex(Integer.valueOf(PolicyConstant.THREE), userRiskPolicyArgs.getPassword())){
            return RiskLevel.RISK_HIGH;
        }

        if(PolicyConstant.RISK_FACTOR_ONE.equals(factor)){
            return riskFactorOne(userRiskPolicyArgs.getPassword());
        }

        if(PolicyConstant.RISK_FACTOR_TWO.equals(factor)){
            return riskFactorTwo(userRiskPolicyArgs.getPassword());
        }

        if(PolicyConstant.RISK_FACTOR_THREE.equals(factor)){
            return riskFactorThree(userRiskPolicyArgs.getPassword());
        }

        if(PolicyConstant.RISK_FACTOR_FOUR.equals(factor)){
            return riskFactorFour(userRiskPolicyArgs.getPassword());
        }

        if(PolicyConstant.RISK_FACTOR_FIVE.equals(factor)){
            return riskFactorFive(userRiskPolicyArgs.getPassword());
        }

        if(PolicyConstant.RISK_FACTOR_SIX.equals(factor)){
            return riskFactorSix(userRiskPolicyArgs.getPassword());
        }

        if(PolicyConstant.RISK_FACTOR_SEVEN.equals(factor)){
            return riskFactorSeven(userRiskPolicyArgs.getPassword());
        }

        return RiskLevel.RISK_NONE;
    }

    private RiskLevel riskFactorOne(String password){
        if(SimplePasswordUtil.checkRepeat(password)){
            return RiskLevel.RISK_HIGH;
        }
        return RiskLevel.RISK_NONE;
    }

    private RiskLevel riskFactorTwo(String password){
        if(SimplePasswordUtil.checkDigitIncrement(password)){
            return RiskLevel.RISK_HIGH;
        }
        return riskFactorOne(password);
    }

    private RiskLevel riskFactorThree(String password){
        if(SimplePasswordUtil.checkDigitDecrement(password)){
            return RiskLevel.RISK_HIGH;
        }
        return riskFactorTwo(password);
    }

    private RiskLevel riskFactorFour(String password){
        if(SimplePasswordUtil.checkLowercaseIncrement(password)){
            return RiskLevel.RISK_HIGH;
        }
        return riskFactorThree(password);
    }

    private RiskLevel riskFactorFive(String password){
        if(SimplePasswordUtil.checkUppercaseIncrement(password)){
            return RiskLevel.RISK_HIGH;
        }
        return riskFactorFour(password);
    }

    private RiskLevel riskFactorSix(String password){
        if(SimplePasswordUtil.checkLowercaseDecrement(password)){
            return RiskLevel.RISK_HIGH;
        }
        return riskFactorFive(password);
    }

    private RiskLevel riskFactorSeven(String password){
        if(SimplePasswordUtil.checkUppercaseDecrement(password)){
            return RiskLevel.RISK_HIGH;
        }
        return riskFactorSix(password);
    }

}
