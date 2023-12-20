package com.kcwl.common.log.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcwl.common.log.IOpLogger;
import com.kcwl.common.log.LogLevel;
import com.kcwl.common.log.LogMode;
import com.kcwl.common.log.OpLogProperties;
import com.kcwl.common.log.annotation.OpLogger;
import com.kcwl.common.log.dao.UserOperationLogMapper;
import com.kcwl.common.log.entity.UserOperationLog;
import com.kcwl.common.log.enums.DesensitizedTypeEnum;
import com.kcwl.common.log.pojo.LogJoinPointContext;
import com.kcwl.common.log.pojo.MethodResult;
import com.kcwl.common.log.util.MultiDataSourceHelper;
import com.kcwl.ddd.domain.entity.UserAgent;
import com.kcwl.ddd.infrastructure.api.ResponseMessage;
import com.kcwl.ddd.infrastructure.session.SessionData;
import com.kcwl.framework.utils.DesensitizedComponent;
import com.kcwl.framework.utils.JsonUtil;
import com.kcwl.framework.utils.KcBeanConverter;
import com.kcwl.framework.utils.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ckwl
 */
@Slf4j
@Service
public class DbOpLogger implements IOpLogger {
    @Value("${kcwl.common.oplog.data-source:#{null}}")
    private String dataSource;
    @Resource
    UserOperationLogMapper userOperationLogMapper;
    @Resource
    DesensitizedComponent desensitizedComponent;

    @Resource
    Executor loggerExecutor;
    @Resource
    OpLogProperties opLogProperties;

    @Override
    public void record(final LogJoinPointContext context) {
        loggerExecutor.execute(new Runnable() {
            @Override
            public void run() {
                MultiDataSourceHelper.switchAndRun(dataSource, () -> {
                    saveOpLog(context);
                });
            }
        });
    }

    private void saveOpLog(LogJoinPointContext context) {
        try {
            OpLogger aopOpLogger = context.getAopOpLogger();
            MethodResult result = getReturnMsg(context.getResult(), context.getException(), aopOpLogger.mode());
            UserOperationLog userOperationLogInfo = null;
            if (aopOpLogger.level().equals(LogLevel.ERROR)) {
                if ((context.getException() != null) || !result.isSuccess()) {
                    userOperationLogInfo = buildUserOperationLogInfo(context, result);
                }
            } else {
                userOperationLogInfo = buildUserOperationLogInfo(context, result);
            }
            if (userOperationLogInfo != null) {
                userOperationLogMapper.insert(userOperationLogInfo);
            }
        } catch (Exception e) {
            log.error("记录操作日志时发生错误：{}", e.getMessage());
        }
    }

    @SneakyThrows
    private UserOperationLog buildUserOperationLogInfo(LogJoinPointContext context, MethodResult result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OpLogger aopOpLogger = context.getAopOpLogger();
        HashMap<String, Object> argValues = convertToMap(context.getArgs());
        UserOperationLog userOperationLogInfo = new UserOperationLog();
        userOperationLogInfo.setServiceName(context.getServiceName());
        userOperationLogInfo.setMethod(context.getMethodName());
        userOperationLogInfo.setTraceId(context.getTraceId());
        userOperationLogInfo.setBizTag(aopOpLogger.tag());
        userOperationLogInfo.setCreateTime(context.getCreateDate());
        // 操作类型
        userOperationLogInfo.setOperType(aopOpLogger.operType().getCode());

        if (argValues.size() > 0) {
            sensitiveFieldFilter(argValues);
            userOperationLogInfo.setMessage(getLogMessage(argValues, aopOpLogger));
        } else {
            userOperationLogInfo.setMessage(aopOpLogger.message());
        }

        UserAgent requestUserAgent = context.getRequestUserAgent();
        if (requestUserAgent != null) {
            userOperationLogInfo.setProduct(requestUserAgent.getProduct());
            userOperationLogInfo.setPlatformNo(requestUserAgent.getPlatform());
            userOperationLogInfo.setKctoken(requestUserAgent.getKcToken());
            userOperationLogInfo.setKctrace(requestUserAgent.getKcTrace());
            userOperationLogInfo.setUserIp(requestUserAgent.getClientIp());
        }

        SessionData sessionData = context.getSessionData();
        if (sessionData != null) {
            userOperationLogInfo.setUserId(sessionData.getUserId());
            userOperationLogInfo.setUserName(sessionData.getUserName());
            userOperationLogInfo.setUserRealName(sessionData.getRealName());
            String mobile = desensitizedComponent.mobile(sessionData.getMobile());
            userOperationLogInfo.setTelPhoneNo(StringUtil.defaultString(mobile, ""));
        }

        if (aopOpLogger.mode().equals(LogMode.DETAIL)) {
            userOperationLogInfo.setParams(StrUtil.subPre(mapper.writeValueAsString(argValues), 10_000));
        } else {
            result.setData("");
        }
        if (context.getException() != null) {
            result.setData(context.getException().getMessage());
        }
        userOperationLogInfo.setResult(StrUtil.subPre(mapper.writeValueAsString(result), 9_990));

        return userOperationLogInfo;
    }

    private String getLogMessage(HashMap<String, Object> argMap, OpLogger aopOpLogger) {
        String regEx = "\\$\\{([^}]*)\\}";
        String message = aopOpLogger.message();
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(message);

        while (matcher.find()) {
            String strVal;
            // 需要脱敏处理
            if (matcher.group(1).startsWith("DLP:")) {
                strVal = dataLossPrevention(argMap, matcher.group(1));
            } else {
                Object objVal = argMap.get(matcher.group(1));
                strVal = Convert.toStr(objVal, StringUtil.EMPTY);
            }
            message = message.replace(matcher.group(0), strVal);
        }
        return message;
    }

    private String dataLossPrevention(HashMap<String, Object> argMap, String messageField) {
        String[] splitInfo = messageField.split(":");
        if (splitInfo.length != 3) {
            return Convert.toStr(argMap.get(messageField), StringUtil.EMPTY);
        }
        String originValue = Convert.toStr(argMap.get(splitInfo[2]), StringUtil.EMPTY);
        if (StringUtil.isBlank(originValue)) {
            return "";
        }
        String desensitizedMethod = splitInfo[1];
        return desensitizedData(desensitizedMethod, originValue);
    }

    public String desensitizedData(String desensitizedTypeCode, String originValue) {
        DesensitizedUtil.DesensitizedType desensitizedType = DesensitizedTypeEnum.getDesensitizedType(desensitizedTypeCode);
        if (Objects.isNull(desensitizedType)) {
            return originValue;
        }
        return DesensitizedUtil.desensitized(originValue, desensitizedType);
    }

    private MethodResult getReturnMsg(Object result, Exception ex, String logMode) {
        MethodResult returnMsg = new MethodResult();
        if (result != null) {
            if (result instanceof ResponseMessage) {
                returnMsg.setResponseMessage((ResponseMessage) result);
            } else {
                returnMsg.setMessage("");
                returnMsg.setData(result);
            }
        } else {
            returnMsg.setCode(MethodResult.ERROR);
            returnMsg.setMessage("系统异常");
        }
        return returnMsg;
    }

    public HashMap<String, Object> convertToMap(Object[] argValues) {
        int indexKey = 0;
        HashMap<String, Object> argsMap = new HashMap<String, Object>(32);
        if (argValues != null) {
            for (Object argValue : argValues) {
                if ((argValue != null) && (!isExcludeClass(argValue))) {
                    if (ClassUtils.isPrimitiveWrapper(argValue.getClass())) {
                        argsMap.put(String.valueOf(indexKey++), argValue);
                    } else if (argValue instanceof String) {
                        argsMap.put(String.valueOf(indexKey++), argValue);
                    } else if (argValue instanceof Map) {
                        KcBeanConverter.copyToMap((Map) argValue, argsMap, true);
                    } else {
                        KcBeanConverter.toHashMap(argValue, argsMap, true);
                    }
                }
            }
        }
        return argsMap;
    }
    private boolean isExcludeClass(Object argValue) {
        if (argValue instanceof ServletResponse || argValue instanceof ServletRequest) {
            return true;
        }
        return false;
    }
    private void sensitiveFieldFilter(HashMap<String, Object> argMap) {
        if ( opLogProperties.isCheckSensitiveField() ) {
            for (Map.Entry<String, Object> entry : argMap.entrySet()) {
                if ( opLogProperties.includeSensitiveField(entry.getKey()) ) {
                    entry.setValue(opLogProperties.getSensitiveMask());
                }
            }
        }
     }
}
