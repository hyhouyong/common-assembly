package com.kcwl.common.risk.strategy.action.impl;

import com.kcwl.common.risk.enums.UserRiskErrorEnum;
import com.kcwl.common.risk.exception.UserRiskException;
import com.kcwl.common.risk.strategy.action.IRiskAction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * “最近一段使用过的密码” 中断
 * @author ckwl
 */
@Component
public class UsedPasswordInterruptAction implements IRiskAction {

    @Override
    public void dispose(String param,Object actionArgs) {
        if(StringUtils.isBlank(param)){
            throw new UserRiskException(UserRiskErrorEnum.USED_PASSWORD.getCode(), UserRiskErrorEnum.USED_PASSWORD.getMsg());
        } else {
            throw new UserRiskException(UserRiskErrorEnum.USED_PASSWORD.getCode(), param);
        }
    }
}
