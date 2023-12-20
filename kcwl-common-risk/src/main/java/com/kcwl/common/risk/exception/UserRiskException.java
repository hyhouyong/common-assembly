package com.kcwl.common.risk.exception;

import com.kcwl.common.risk.enums.UserRiskErrorEnum;
import com.kcwl.ddd.infrastructure.exception.BaseException;

/**
 * 风险策略自定义异常
 * @author ckwl
 */
public class UserRiskException extends BaseException {

    private static final long serialVersionUID = 1;

    private static final String PRE_CODE = "00";

    public UserRiskException(UserRiskErrorEnum userRiskErrorEnum) {
        super(PRE_CODE+userRiskErrorEnum.getCode(), userRiskErrorEnum.getMsg());
    }

    /**
     * 密码错误
     * @param userRiskErrorEnum
     * @param count
     */
    public UserRiskException(UserRiskErrorEnum userRiskErrorEnum, Integer count) {
        super(PRE_CODE+userRiskErrorEnum.getCode(), userRiskErrorEnum.getMsg()+count+"次，连续输入错误十次后账号将被锁定十分钟");
    }


    public UserRiskException(String code, String message) {
        super(PRE_CODE+code, message);
    }

    public UserRiskException(String code, String message, Throwable cause) {
        super(PRE_CODE+code, message, cause);
    }
}
