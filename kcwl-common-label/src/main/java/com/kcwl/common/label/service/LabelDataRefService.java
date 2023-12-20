package com.kcwl.common.label.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kcwl.common.label.pojo.po.LabelDataRefPo;
import com.kcwl.support.label.command.LabelDataRefRemoveCommand;
import com.kcwl.support.label.command.LabelDataRefSaveBatchCommand;
import com.kcwl.support.label.command.LabelDataRefSaveCommand;
import com.kcwl.support.label.dto.LabelDataRefDto;
import com.kcwl.support.label.dto.LabelInfoAvailableDto;
import com.kcwl.support.label.query.LabelDataRefQuery;
import com.kcwl.support.label.query.LabelInfoAvailableQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 数据与标签关联表 服务类
 * </p>
 *
 * @author renyp
 * @since 2023-03-29
 */
public interface LabelDataRefService extends IService<LabelDataRefPo> {

    /**
     * 数据打标
     */
    Boolean save(@Valid LabelDataRefSaveCommand labelDataRefSaveCommand);

    /**
     * 数据批量打标
     */
    Boolean saveBatchDataLabelRef(@Valid @NotNull LabelDataRefSaveBatchCommand labelDataRefSaveBatchCommand);
    /**
     * 删除指定标签
     */
    Boolean removeRelatedLabel(@Valid LabelDataRefRemoveCommand labelDataRefRemoveCommand);

    /**
     * 批量清除标签
     */
    Boolean removeRelatedLabelAll(@Valid LabelDataRefRemoveCommand labelDataRefRemoveCommand);

    /**
     * 查询数据标签
     */
    List<LabelDataRefDto> listRelatedLabel(@Valid LabelDataRefQuery labelDataRefQuery);

    /**
     *查询标签可用标签
     */
    List<LabelInfoAvailableDto> availableList(@Valid LabelInfoAvailableQuery labelInfoAvailableQuery);

}
