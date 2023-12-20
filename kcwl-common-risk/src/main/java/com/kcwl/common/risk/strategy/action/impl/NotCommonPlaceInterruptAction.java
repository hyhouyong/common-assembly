package com.kcwl.common.risk.strategy.action.impl;

import com.kcwl.common.risk.entity.UserRiskPolicyArgs;
import com.kcwl.common.risk.enums.SendTypeEnum;
import com.kcwl.common.risk.enums.UserRiskErrorEnum;
import com.kcwl.common.risk.exception.UserRiskException;
import com.kcwl.common.risk.strategy.action.IRiskAction;
import com.kcwl.framework.utils.StringUtil;
import com.kcwl.notification.interfaces.api.ISmsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 异地密码登录中断
 * @author ckwl
 */
@Component
public class NotCommonPlaceInterruptAction implements IRiskAction {

    @Autowired
    private ISmsService smsService;

    @Override
    public void dispose(String param,Object actionArgs) {
        UserRiskPolicyArgs userRiskPolicyArgs = (UserRiskPolicyArgs)actionArgs;
        if(userRiskPolicyArgs.getSendWarningNoticeFlag() != null && userRiskPolicyArgs.getSendWarningNoticeFlag() && StringUtil.isNotBlank(userRiskPolicyArgs.getMobile())){
            Map noticeParam = new HashMap(1);
            String substring = userRiskPolicyArgs.getMobile().substring(userRiskPolicyArgs.getMobile().length() - 4);
            noticeParam.put("mobile",substring);
            // 发送预警短信
            smsService.sendMessages(userRiskPolicyArgs.getMobile(), SendTypeEnum.NOT_COMMON_PLACE.getSendType(),noticeParam);
        }
        if(StringUtils.isBlank(param)){
            throw new UserRiskException(UserRiskErrorEnum.NOT_COMMON_PLACE.getCode(), UserRiskErrorEnum.NOT_COMMON_PLACE.getMsg());
        } else {
            throw new UserRiskException(UserRiskErrorEnum.NOT_COMMON_PLACE.getCode(), param);
        }
    }
}
