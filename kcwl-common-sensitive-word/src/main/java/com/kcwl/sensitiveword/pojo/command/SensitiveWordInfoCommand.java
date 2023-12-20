package com.kcwl.sensitiveword.pojo.command;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author renyp
 * @since 2023-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SensitiveWordInfo对象", description="")
public class SensitiveWordInfoCommand implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "敏感词")
    private String sensitiveWord;

    @ApiModelProperty(value = "敏感级别")
    private String sensitiveLevel;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "状态")
    private Integer status;


}
