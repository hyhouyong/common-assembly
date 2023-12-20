package com.kcwl.common.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kcwl.common.log.enums.ApiLogType;
import com.kcwl.common.log.enums.RetryStatus;
import com.kcwl.common.log.enums.RetryWaitStrategies;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 接口日志
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("kcwl_api_log")
public class ApiLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 记录类型
    @TableField("log_type")
    private ApiLogType type;

    // 业务标识（无功能作用，仅作区分）
    private String bizCode;

    // 创建时间
    private Date createTime;

    // 修改时间
    private Date updateTime;

    // 环境
    private String env;

    // 服务名称（调用方）
    private String serviceName;

    // 目标服务名称（被调用服务）
    private String destServiceName;

    // 平台
    private String platformNo;

    // 产品类型
    private String product;

    // SkyWalking TraceId
    private String traceId;

    // 异常类型
    private String exceptionType;

    // 异常信息
    private String exceptionMessage;

    // 请求方法
    private String requestMethod;

    // 当前请求地址（后面的资源地址）
    private String requestUri;

    // 请求头（JSON）
    private String requestHeaders;

    // 内容类型
    private String contentType;

    // 参数（url、form）
    private String requestParam;

    // 请求体（application/json body）
    private String body;

    // 返回响应码
    private Integer responseCode;

    // 返回响应体（content-type application/json 时记录）
    private String responseBody;

    // =========================
    // 重试相关

    // 重试状态、无需重试，
    private RetryStatus retryStatus;

    // 重试总次数
    private Integer retryTimes;

    // 已重试次数
    private Integer retryCount;

    // 重试 http 方法 （GET、POST）
    private String retryMethod;

    // TODO: 2021/8/18 等待时间，接口可能 5秒钟，2分钟， 有个任务调度去管理异常关闭任务
    //  现在在 job 写死 5分钟

    // 重试地址（绝对请求路径）
    private String retryUrl;

    // 重试等待策略
    private RetryWaitStrategies retryWaitStrategy;

    // 重试等待间隔
    private Integer retryWaitInterval;

}
