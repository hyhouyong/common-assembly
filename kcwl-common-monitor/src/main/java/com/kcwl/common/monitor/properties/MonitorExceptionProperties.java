package com.kcwl.common.monitor.properties;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.kcwl.common.monitor.enums.AlarmMode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author ckwl
 */
@Data
@Component
@ConfigurationProperties(prefix = "kcwl.monitor")
public class MonitorExceptionProperties {

    /**
     * 消息发送间隔时间（mS）
     */
    private Integer interval = 10000;

    private ExceptionAlarmConfig alarm = new ExceptionAlarmConfig();

    @Data
    public static class ExceptionAlarmConfig{
        private boolean enable = false;
        private CopyOnWriteArraySet<String> fatalList;
        private CopyOnWriteArraySet<String> ignoreList;

        private int alarmMode= AlarmMode.ALARM_NONE.getMode();

        public boolean isAlarmException(String exception) {
            if ( alarmMode == AlarmMode.ALARM_NONE.getMode() || isIgnoreException(exception) ) {
                return false;
            }
            if ( alarmMode == AlarmMode.ALARM_ALL.getMode() ){
                return true;
            }
            return (alarmMode == AlarmMode.ALARM_FATAL.getMode()) && (isFatalException(exception));
        }

        private boolean isFatalException(String exception) {
            return (fatalList != null) && fatalList.contains(exception);
        }

        private boolean isIgnoreException(String exception) {
            return (ignoreList != null) && ignoreList.contains(exception);
        }
    }
}
