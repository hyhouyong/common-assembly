package com.kcwl.common.log.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kcwl.common.log.dao.ApiRetryJobMapper;
import com.kcwl.common.log.entity.ApiRetryJob;
import org.springframework.stereotype.Service;

/**
 * 接口 重试任务
 */
@Service
public class ApiRetryJobService extends ServiceImpl<ApiRetryJobMapper, ApiRetryJob> {
}
