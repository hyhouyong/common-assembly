package com.kcwl.common.log.pojo;

import com.kcwl.common.log.annotation.OpLogger;
import com.kcwl.ddd.domain.entity.UserAgent;
import com.kcwl.ddd.infrastructure.session.SessionData;
import lombok.Data;

import java.util.Date;

@Data
public class LogJoinPointContext {
    OpLogger aopOpLogger;
    String serviceName;
    String methodName;
    Object[] args;
    String traceId;
    Object result;
    Exception exception;
    UserAgent requestUserAgent;
    SessionData sessionData;
    Date createDate;
}
