package com.kcwl.common.label.utils.validate.labeldataref;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.kcwl.common.label.pojo.po.LabelDataRefPo;
import com.kcwl.common.label.service.LabelDataRefService;
import com.kcwl.support.label.command.LabelDataRefSaveCommand;
import com.kcwl.support.label.enums.IdentityRelatedLabelEnum;
import com.kcwl.support.label.utils.validate.CheckNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 重复打标 校验
 * </p>
 *
 * @author renyp
 * @since 2023/4/17 13:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SameLabelDataRefCheckNode implements CheckNode<LabelDataRefSaveCommand> {

    private final LabelDataRefService labelDataRefService;

    @Override
    public void accept(LabelDataRefSaveCommand labelDataRefSaveCommand) {
        List<Long> dataLabels = labelDataRefService.lambdaQuery()
                .eq(StrUtil.isNotBlank(labelDataRefSaveCommand.getDataType()), LabelDataRefPo::getDataType, labelDataRefSaveCommand.getDataType())
                .eq(StrUtil.isNotBlank(labelDataRefSaveCommand.getDataIdentifier()), LabelDataRefPo::getDataIdentifier, labelDataRefSaveCommand.getDataIdentifier())
                .in(CollUtil.isNotEmpty(labelDataRefSaveCommand.getLabelInfo()), LabelDataRefPo::getLabelId, labelDataRefSaveCommand.getLabelInfo().stream().map(LabelDataRefSaveCommand.LabelInfoSimple::getLabelId).collect(Collectors.toList()))
                .eq(LabelDataRefPo::getCreatorIdentity, labelDataRefSaveCommand.getCreatorIdentity())
                .eq(StrUtil.isNotBlank(labelDataRefSaveCommand.getAuthCode()) && IdentityRelatedLabelEnum.SHIPPER.getCode().equals(labelDataRefSaveCommand.getCreatorIdentity()),
                        LabelDataRefPo::getAuthCode, labelDataRefSaveCommand.getAuthCode())
                .list().stream()
                .map(LabelDataRefPo::getLabelId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(dataLabels)) {
            log.warn("数据 ：{} 重复打标：{}", String.format("%s : %s", labelDataRefSaveCommand.getDataType(), labelDataRefSaveCommand.getDataIdentifier()), dataLabels);
            labelDataRefSaveCommand.setLabelInfo(labelDataRefSaveCommand.getLabelInfo().stream().filter(labelInfo -> !dataLabels.contains(labelInfo.getLabelId())).collect(Collectors.toList()));
        }

    }
}
