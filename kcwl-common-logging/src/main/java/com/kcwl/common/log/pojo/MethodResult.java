package com.kcwl.common.log.pojo;

import com.kcwl.ddd.infrastructure.api.ResponseMessage;
import lombok.Data;

@Data
public class MethodResult {
    public static final String SUCCESS = "200";
    public static final String ERROR = "999";
    private String code = SUCCESS;
    private String message;
    private Object data;

    public void setResponseMessage(ResponseMessage responseMessage) {
        this.code = responseMessage.getCode();
        this.message = responseMessage.getMessage();
        this.data = responseMessage.getResult();
    }

    public boolean isSuccess() {
        return SUCCESS.equals(code);
    }
}
