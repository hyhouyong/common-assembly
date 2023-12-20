package com.kcwl.sensitiveword.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 敏感词 dto
 * </p>
 *
 * @author renyp
 * @since 2023-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SensitiveWordInfo dto", description="")
public class SensitiveWordInfoDto implements Serializable {


    @ApiModelProperty(value = "敏感词唯一标识")
    private Long id;

    @ApiModelProperty(value = "敏感词")
    private String sensitiveWord;

    @ApiModelProperty(value = "敏感级别")
    private String sensitiveLevel;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
