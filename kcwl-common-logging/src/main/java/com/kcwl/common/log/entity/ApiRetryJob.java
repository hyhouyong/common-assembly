package com.kcwl.common.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 失败接口 重试任务
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("kcwl_api_retry_job")
public class ApiRetryJob {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 创建时间
    private Date createTime;

    // 修改时间
    private Date updateTime;

    // 环境
    private String env;

    // 错误日志ID
    private Long logId;

    // 上一次重试时间（毫秒）
    private Long lastRetryTime;

    // 下一次重试时间（毫秒）
    private Long nextRetryTime;

    // 是否启动中
    private Boolean isRunning;

    // 数据版本号
    private Integer revision;

}
