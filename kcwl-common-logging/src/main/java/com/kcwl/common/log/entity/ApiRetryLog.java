package com.kcwl.common.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kcwl.common.log.enums.RetryResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 失败接口 重试日志
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("kcwl_api_retry_log")
public class ApiRetryLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    // ApiLog.id
    private Long logId;

    // ApiRetryJob.id
    private Long jobId;

    // 创建时间 与 修改时间
    private Date createTime;
    private Date updateTime;

    // 第几次重试
    private Integer retryTime;

    // SkyWalking TraceId
    private String traceId;

    // 重试结果
    private RetryResultType result;

    // 返回响应码
    private Integer responseCode;

    // 返回响应体
    private String responseBody;

}
