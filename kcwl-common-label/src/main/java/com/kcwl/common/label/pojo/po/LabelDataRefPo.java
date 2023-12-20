package com.kcwl.common.label.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 数据与标签关联表
 * </p>
 *
 * @author renyp
 * @since 2023-03-29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "LabelDataRef对象", description = "数据与标签关联表")
@TableName(value = "label_data_ref", autoResultMap = true)
public class LabelDataRefPo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "数据类型")
    private String dataType;

    @ApiModelProperty(value = "数据唯一标识")
    private String dataIdentifier;

    @ApiModelProperty(value = "标签id")
    private Long labelId;

    @ApiModelProperty(value = "标签名称")
    private String labelName;

    @ApiModelProperty(value = "标签类型名称")
    private String labelTypeName;

    @ApiModelProperty(value = "标签可见权限")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> accessScope;

    @ApiModelProperty(value = "过期时间")
    private Date expiredTime;

    @ApiModelProperty(value = "评分")
    private Integer score;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "创建人身份")
    private String creatorIdentity;

    @ApiModelProperty(value = "授权码")
    private String authCode;

    @ApiModelProperty(value = "修改人id")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "产品端")
    private String product;

    @ApiModelProperty(value = "平台编码")
    private String platformNo;


}
