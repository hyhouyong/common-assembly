package com.kcwl.common.monitor;

import com.kcwl.common.monitor.alarm.ExceptionAlarmManager;
import com.kcwl.ddd.domain.event.ExceptionEvent;
import com.kcwl.ddd.domain.event.IExceptionEventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ckwl
 */
@Component
public class ExceptionEventMonitorListener implements IExceptionEventListener {

    @Resource
    ExceptionAlarmManager exceptionAlarmManager;

    @Override
    public void onEvent(ExceptionEvent event) {
        exceptionAlarmManager.alarm(event);
    }
}
