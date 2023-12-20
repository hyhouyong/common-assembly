package com.kcwl.common.risk.strategy.action.impl;

import com.kcwl.common.risk.strategy.action.IRiskAction;
import com.kcwl.common.risk.exception.UserRiskException;
import com.kcwl.ddd.infrastructure.api.CommonCode;
import org.springframework.stereotype.Component;

/**
 * 通用中断流程
 * @author ckwl
 */
@Component
public class InterruptAction implements IRiskAction {

    @Override
    public void dispose(String param,Object actionArgs) {
        throw new UserRiskException(CommonCode.REQUEST_UNDER_RISK.getCode(), param);
    }
}
