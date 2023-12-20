package com.kcwl.sensitiveword.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 内存中 敏感词 信息
 * </p>
 *
 * @author renyp
 * @since 2023-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@ApiModel(value = "SensitiveWordInfo", description = "")
@AllArgsConstructor
@NoArgsConstructor
public class SensitiveWordInfoSlim implements Serializable {


    @ApiModelProperty(value = "敏感词")
    private String sensitiveWord;

    @ApiModelProperty(value = "敏感级别")
    private String sensitiveLevel;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

}
