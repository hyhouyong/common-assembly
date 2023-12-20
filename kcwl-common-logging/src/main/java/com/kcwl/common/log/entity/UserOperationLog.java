package com.kcwl.common.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
public class UserOperationLog extends Model<UserOperationLog> {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String serviceName;

    private String product;

    private String bizTag;

    private String kctoken;

    private String kctrace;

    private String traceId;

    private String method;

    private String message;

    private String params;

    private String result;

    private long userId;

    private String userName;

    private Date createTime;

    private String platformNo;

    private String operType;

    private String telPhoneNo;

    private String userIp;

    private String userRealName;
}
