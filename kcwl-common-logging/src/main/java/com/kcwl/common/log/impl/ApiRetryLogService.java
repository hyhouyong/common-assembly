package com.kcwl.common.log.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kcwl.common.log.dao.ApiRetryLogMapper;
import com.kcwl.common.log.entity.ApiRetryLog;
import org.springframework.stereotype.Service;

/**
 * 接口 重试日志
 */
@Service
public class ApiRetryLogService extends ServiceImpl<ApiRetryLogMapper, ApiRetryLog> {
}
