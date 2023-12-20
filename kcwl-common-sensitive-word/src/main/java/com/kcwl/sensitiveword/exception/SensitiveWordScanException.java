package com.kcwl.sensitiveword.exception;

import com.kcwl.ddd.infrastructure.api.CommonCode;
import com.kcwl.ddd.infrastructure.exception.BaseException;

/**
 * <p>
 * 敏感词检测异常
 * </p>
 *
 * @author renyp
 * @since 2023/6/1 17:49
 */
public class SensitiveWordScanException extends BaseException {
    public SensitiveWordScanException() {
        super(CommonCode.CONTAIN_SENSITIVE_WORDS.getCode(), CommonCode.CONTAIN_SENSITIVE_WORDS.getDescription());
    }

    public SensitiveWordScanException(String message) {
        super(CommonCode.CONTAIN_SENSITIVE_WORDS.getCode(), message);
    }
}
