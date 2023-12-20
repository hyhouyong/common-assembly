package com.kcwl.common.risk.strategy.action.impl;

import com.kcwl.common.risk.enums.UserRiskErrorEnum;
import com.kcwl.common.risk.exception.UserRiskException;
import com.kcwl.common.risk.strategy.action.IRiskAction;
import com.kcwl.framework.utils.StringUtil;
import org.springframework.stereotype.Component;

/**
 * 弱密码中断流程
 * @author ckwl
 */
@Component
public class WeakPasswordInterruptAction implements IRiskAction {

    @Override
    public void dispose(String param,Object actionArgs) {
        if(StringUtil.isNotBlank(param)){
            throw new UserRiskException(UserRiskErrorEnum.WEAK_PASSWORD.getCode(), param);
        }
        throw new UserRiskException(UserRiskErrorEnum.WEAK_PASSWORD);
    }

}
