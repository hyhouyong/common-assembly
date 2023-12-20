package com.kcwl.common.monitor.alarm;

import com.github.benmanes.caffeine.cache.Cache;
import com.kcwl.common.monitor.properties.MonitorExceptionProperties;
import com.kcwl.ddd.domain.entity.UserAgent;
import com.kcwl.ddd.domain.event.ExceptionEvent;
import com.kcwl.ddd.infrastructure.session.SessionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author ckwl
 */
@Component
public class ExceptionAlarmManager {

    private static final int INIT_MESSAGE_LEN = 1024;

    @Value("${spring.application.name:appName}")
    private String appName;

    @Value("${spring.application.profile:pro}")
    private String profile;

    @Resource
    Cache<String, Long> recentAlarmException;

    @Resource
    MonitorExceptionProperties monitorExceptionProperties;

    @Resource
    IAlarmChannel alarmChannel;

    @Resource
    Executor alarmExecutor;

    public void alarm(ExceptionEvent event){
        MonitorExceptionProperties.ExceptionAlarmConfig alarmConfig = monitorExceptionProperties.getAlarm();

        if ( alarmConfig.isEnable() && event.getException() != null ) {
            String exceptionName = event.getException().getClass().getSimpleName();
            if (alarmConfig.isAlarmException(exceptionName)) {
                Object recentException = recentAlarmException.getIfPresent(exceptionName);
                if (recentException == null) {
                    asyncAlarm(exceptionName,  event.getException());
                    recentAlarmException.put(exceptionName, System.currentTimeMillis());
                }
            }
        }
    }

    private void asyncAlarm(String exceptionName, Exception e){
        UserAgent userAgent = SessionContext.getRequestUserAgent();
        alarmExecutor.execute(() -> alarmChannel.alarm(null, buildNotifyMessage(userAgent,exceptionName, e)));
    }

    private String buildNotifyMessage(UserAgent userAgent, String exceptionName, Exception e) {
        StringBuilder sb = new StringBuilder(INIT_MESSAGE_LEN);
        sb.append("【").append(this.profile).append("】");
        sb.append("【").append(this.appName).append("】");
        sb.append("【");
        if ( userAgent != null ) {
            sb.append(userAgent.getKcTrace());
        } else {
            sb.append("KTID:NONE");
        }
        sb.append("】发生异常：").append(exceptionName);
        sb.append(e.getMessage());
        return sb.toString();
    }
}

