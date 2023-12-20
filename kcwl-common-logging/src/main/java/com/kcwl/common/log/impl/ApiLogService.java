package com.kcwl.common.log.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kcwl.common.log.dao.ApiLogMapper;
import com.kcwl.common.log.dao.ApiRetryJobMapper;
import com.kcwl.common.log.entity.ApiLog;
import com.kcwl.common.log.entity.ApiRetryJob;
import com.kcwl.common.log.enums.RetryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 接口 请求日志
 */
@Service
public class ApiLogService extends ServiceImpl<ApiLogMapper, ApiLog> {

    public final static String IS_RETRY_HEADER = "kc-api-retry";

    @Resource
    private ApiRetryJobMapper apiRetryJobMapper;

    /**
     * 保存日志
     * 生成重试任务
     */
    @Transactional
    public void saveLog(ApiLog apiLog) {
        getBaseMapper().insert(apiLog);

        // 开启重试任务
        if (apiLog.getRetryStatus() == RetryStatus.PROCESSING) {

            ApiRetryJob apiRetryJob = ApiRetryJob.builder()
                    .env(apiLog.getEnv())
                    .logId(apiLog.getId())
                    .lastRetryTime(0L)
                    .nextRetryTime(apiLog.getRetryWaitStrategy().nextRetryTime(System.currentTimeMillis(), apiLog.getRetryCount(), apiLog.getRetryWaitInterval()))
                    .isRunning(false)
                    .revision(1)
                    .build();
            apiRetryJobMapper.insert(apiRetryJob);

        }

    }

}
