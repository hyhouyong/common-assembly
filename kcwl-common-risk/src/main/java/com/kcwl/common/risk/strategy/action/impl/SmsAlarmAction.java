package com.kcwl.common.risk.strategy.action.impl;

import com.kcwl.common.risk.strategy.action.IRiskAction;
import org.springframework.stereotype.Component;

/**
 * 发送短信
 * @author ckwl
 */
@Component
public class SmsAlarmAction implements IRiskAction {

    @Override
    public void dispose(String param,Object actionArgs) {
    }

}
