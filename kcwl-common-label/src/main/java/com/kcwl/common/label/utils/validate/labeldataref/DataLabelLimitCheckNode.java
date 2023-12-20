package com.kcwl.common.label.utils.validate.labeldataref;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.kcwl.common.label.pojo.po.LabelDataRefPo;
import com.kcwl.common.label.service.LabelDataRefService;
import com.kcwl.support.label.command.LabelDataRefSaveCommand;
import com.kcwl.support.label.constants.LabelRelatedConstant;
import com.kcwl.support.label.enums.IdentityRelatedLabelEnum;
import com.kcwl.support.label.exceptions.LabelDuplicateException;
import com.kcwl.support.label.exceptions.LabelTooManyException;
import com.kcwl.support.label.utils.validate.CheckNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * <p>
 * 数据打标上限校验
 * </p>
 *
 * @author renyp
 * @since 2023/4/4 16:01
 */
@Slf4j
@Service
public class DataLabelLimitCheckNode implements CheckNode<LabelDataRefSaveCommand> {

    @Autowired
    private LabelDataRefService labelDataRefService;

    @Override
    public void accept(LabelDataRefSaveCommand labelDataRefSaveCommand) {
        // 同一业务数据 同一个创健方 不能超过 10 个标签
        if (labelDataRefService.lambdaQuery()
                .eq(StrUtil.isNotEmpty(labelDataRefSaveCommand.getDataType()), LabelDataRefPo::getDataType, labelDataRefSaveCommand.getDataType())
                .eq(StrUtil.isNotEmpty(labelDataRefSaveCommand.getDataIdentifier()), LabelDataRefPo::getDataIdentifier, labelDataRefSaveCommand.getDataIdentifier())
                .eq(StrUtil.isNotEmpty(labelDataRefSaveCommand.getCreatorIdentity()), LabelDataRefPo::getCreatorIdentity, labelDataRefSaveCommand.getCreatorIdentity())
                .eq(StrUtil.isNotEmpty(labelDataRefSaveCommand.getAuthCode()) && IdentityRelatedLabelEnum.SHIPPER.getCode().equals(labelDataRefSaveCommand.getCreatorIdentity()),
                        LabelDataRefPo::getAuthCode, labelDataRefSaveCommand.getAuthCode())
                .count() + labelDataRefSaveCommand.getLabelInfo().size() > LabelRelatedConstant.LABEL_COUNT_LIMIT) {
            log.error("关联标签数量过多，超过阈值，入参：{}", labelDataRefSaveCommand);
            throw new LabelTooManyException();
        }


    }
}
