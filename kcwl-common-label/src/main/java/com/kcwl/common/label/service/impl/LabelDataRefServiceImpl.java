package com.kcwl.common.label.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kcwl.common.label.mapper.LabelDataRefMapper;
import com.kcwl.common.label.pojo.po.LabelDataRefPo;
import com.kcwl.common.label.service.LabelDataRefService;
import com.kcwl.common.label.service.converter.LabelDataRefConverter;
import com.kcwl.common.label.utils.validate.labeldataref.DataLabelLimitCheckNode;
import com.kcwl.common.label.utils.validate.labeldataref.SameLabelDataRefCheckNode;
import com.kcwl.framework.utils.KcBeanConverter;
import com.kcwl.framework.utils.StringUtil;
import com.kcwl.support.label.api.LabelInfoService;
import com.kcwl.support.label.command.LabelDataRefRemoveCommand;
import com.kcwl.support.label.command.LabelDataRefSaveBatchCommand;
import com.kcwl.support.label.command.LabelDataRefSaveCommand;
import com.kcwl.support.label.constants.LabelRelatedConstant;
import com.kcwl.support.label.dto.LabelDataRefDto;
import com.kcwl.support.label.dto.LabelInfoAvailableDto;
import com.kcwl.support.label.enums.IdentityRelatedLabelEnum;
import com.kcwl.support.label.exceptions.LabelMarkForbiddenException;
import com.kcwl.support.label.exceptions.LabelRemoveTooManyException;
import com.kcwl.support.label.query.LabelDataRefQuery;
import com.kcwl.support.label.query.LabelInfoAvailableQuery;
import com.kcwl.support.label.utils.validate.CheckNodeChainBuilder;
import com.kcwl.support.label.utils.validate.labeldataref.ShipperCooperationRemoveCheckNode;
import com.kcwl.support.label.utils.validate.labeldataref.ShipperCooperationSaveCheckNode;
import com.kcwl.support.label.utils.validate.labeldataref.UserIdentityCheckNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据与标签关联表 服务实现类
 * </p>
 *
 * @author renyp
 * @since 2023-03-29
 */
@Service
@Validated
@Slf4j
public class LabelDataRefServiceImpl extends ServiceImpl<LabelDataRefMapper, LabelDataRefPo> implements LabelDataRefService {

    @Autowired
    private LabelInfoService labelInfoService;

    @Override
    public Boolean save(@Valid LabelDataRefSaveCommand labelDataRefSaveCommand) {

        List<LabelDataRefPo> labelDataRefPoList = generateLabelDataRefPos(labelDataRefSaveCommand);
        if (CollUtil.isEmpty(labelDataRefPoList) || this.saveBatch(labelDataRefPoList)) {
            return true;
        }
        throw new LabelMarkForbiddenException("数据打标记录失败！");
    }

    @Override
    public Boolean saveBatchDataLabelRef(@Valid @NotNull LabelDataRefSaveBatchCommand labelDataRefSaveBatchCommand) {
        // 参数校验
        if (!labelDataRefSaveBatchCommand.argsCheckProcess()) {
            return false;
        }
        // LabelDataRefSaveBatchCommand -> LabelDataRefSaveCommand
        List<LabelDataRefSaveCommand> labelSaveCommandList = new ArrayList<>();
        if (CollUtil.isNotEmpty(labelDataRefSaveBatchCommand.getDataInfoList())) {
            labelDataRefSaveBatchCommand.getDataInfoList().forEach(dataInfo -> {
                LabelDataRefSaveCommand labelDataRefSaveCommand = LabelDataRefConverter.INSTANCE.batchCommand2Command(labelDataRefSaveBatchCommand);
                labelDataRefSaveCommand.setDataType(dataInfo.getDataType());
                labelDataRefSaveCommand.setDataIdentifier(dataInfo.getDataIdentifier());
                labelSaveCommandList.add(labelDataRefSaveCommand);
            });
        } else if (StrUtil.isNotBlank(labelDataRefSaveBatchCommand.getDataType()) && CollUtil.isNotEmpty(labelDataRefSaveBatchCommand.getDataIdentifierList())) {
            labelDataRefSaveBatchCommand.getDataIdentifierList().forEach(dataIdentifier -> {
                LabelDataRefSaveCommand labelDataRefSaveCommand = LabelDataRefConverter.INSTANCE.batchCommand2Command(labelDataRefSaveBatchCommand);
                labelDataRefSaveCommand.setDataIdentifier(dataIdentifier);
                labelSaveCommandList.add(labelDataRefSaveCommand);
            });
        } else {
            log.error("批量打标签，数据标识为空，无可打标签的数据， 入参：{}", labelDataRefSaveBatchCommand);
            throw new LabelMarkForbiddenException("数据打标记录失败, 数据标识为空！");
        }

        List<LabelDataRefPo> labelDataRefPos = new ArrayList<>();
        labelSaveCommandList.forEach(labelDataRefSaveCommand -> labelDataRefPos.addAll(generateLabelDataRefPos(labelDataRefSaveCommand)));
        if (CollUtil.isEmpty(labelDataRefPos) || this.saveBatch(labelDataRefPos)) {
            return true;
        }
        throw new LabelMarkForbiddenException("数据打标记录失败！");

    }

    @Validated({LabelDataRefRemoveCommand.Detail.class})
    @Override
    public Boolean removeRelatedLabel(@Valid LabelDataRefRemoveCommand labelDataRefRemoveCommand) {
        CheckNodeChainBuilder.<LabelDataRefRemoveCommand>builder()
                .appendCheckNode(currentParam -> {
                    if (!IdentityRelatedLabelEnum.validateCode(currentParam.getUserIdentity())) {
                        throw new LabelMarkForbiddenException(String.format("参数校验失败，用户身份类型【%s】不合法！", labelDataRefRemoveCommand.getUserIdentity()));
                    }
                })
                .appendCheckNode(ShipperCooperationRemoveCheckNode::new)
                .invokeAll(labelDataRefRemoveCommand);

        LabelDataRefPo currentLabelData = this.lambdaQuery()
                .eq(StrUtil.isNotEmpty(labelDataRefRemoveCommand.getDataType()), LabelDataRefPo::getDataType, labelDataRefRemoveCommand.getDataType())
                .eq(StrUtil.isNotEmpty(labelDataRefRemoveCommand.getDataIdentifier()), LabelDataRefPo::getDataIdentifier, labelDataRefRemoveCommand.getDataIdentifier())
                .eq(Objects.nonNull(labelDataRefRemoveCommand.getLabelId()), LabelDataRefPo::getLabelId, labelDataRefRemoveCommand.getLabelId())
                .eq(StrUtil.isNotEmpty(labelDataRefRemoveCommand.getUserIdentity()), LabelDataRefPo::getCreatorIdentity, labelDataRefRemoveCommand.getUserIdentity())
                .eq(IdentityRelatedLabelEnum.SHIPPER.getCode().equals(labelDataRefRemoveCommand.getUserIdentity()), LabelDataRefPo::getAuthCode, labelDataRefRemoveCommand.getAuthCode())
                .select(LabelDataRefPo::getId)
                .one();

        if (currentLabelData == null) {
            log.error("标签删除失败！入参：{}", labelDataRefRemoveCommand);
            throw new LabelRemoveTooManyException(String.format("标签：%s 不存在，无法删除！", labelDataRefRemoveCommand.getLabelId()));
        }

        return this.removeById(currentLabelData.getId());
    }

    @Override
    public Boolean removeRelatedLabelAll(@Valid LabelDataRefRemoveCommand labelDataRefRemoveCommand) {
        CheckNodeChainBuilder.<LabelDataRefRemoveCommand>builder()
                .appendCheckNode(currentParam -> {
                    if (!IdentityRelatedLabelEnum.validateCode(currentParam.getUserIdentity())) {
                        throw new LabelMarkForbiddenException(String.format("参数校验失败，用户身份类型【%s】不合法！", labelDataRefRemoveCommand.getUserIdentity()));
                    }
                }).appendCheckNode(ShipperCooperationRemoveCheckNode::new)
                .invokeAll(labelDataRefRemoveCommand);

        List<Long> currentRemoveIds = this.lambdaQuery()
                .eq(StrUtil.isNotEmpty(labelDataRefRemoveCommand.getDataType()), LabelDataRefPo::getDataType, labelDataRefRemoveCommand.getDataType())
                .in(CollUtil.isNotEmpty(labelDataRefRemoveCommand.getDataIdentifierList()), LabelDataRefPo::getDataIdentifier, labelDataRefRemoveCommand.getDataIdentifierList())
                .eq(StrUtil.isNotEmpty(labelDataRefRemoveCommand.getUserIdentity()), LabelDataRefPo::getCreatorIdentity, labelDataRefRemoveCommand.getUserIdentity())
                .eq(IdentityRelatedLabelEnum.SHIPPER.getCode().equals(labelDataRefRemoveCommand.getUserIdentity()), LabelDataRefPo::getAuthCode, labelDataRefRemoveCommand.getAuthCode())
                .eq(Objects.nonNull(labelDataRefRemoveCommand.getLabelId()), LabelDataRefPo::getLabelId, labelDataRefRemoveCommand.getLabelId())
                .select(LabelDataRefPo::getId)
                .list().stream()
                .map(LabelDataRefPo::getId).collect(Collectors.toList());

        if (CollUtil.isEmpty(currentRemoveIds)) {
            log.warn("标签批量清除失败！未检索到可移除标签，入参：{}", labelDataRefRemoveCommand);
            return true;
        }
        if (currentRemoveIds.size() > LabelRelatedConstant.LABEL_COUNT_LIMIT * labelDataRefRemoveCommand.getDataIdentifierList().size()) {
            log.error("一次性删除标签过多，入参:{}", labelDataRefRemoveCommand);
            throw new LabelRemoveTooManyException();
        }

        return this.removeByIds(currentRemoveIds);
    }

    @Override
    public List<LabelDataRefDto> listRelatedLabel(@Valid LabelDataRefQuery labelDataRefQuery) {
        CheckNodeChainBuilder.<LabelDataRefQuery>builder()
                .appendCheckNode(currentParam -> {
                    if (StringUtil.isBlank(currentParam.getDataIdentifier()) && CollectionUtil.isEmpty(currentParam.getDataIdentifierList())) {
                        throw new LabelMarkForbiddenException("禁止不传业务id查询！");
                    }
                })
                .invokeAll(labelDataRefQuery);

        LambdaQueryChainWrapper<LabelDataRefPo> queryChainWrapper = this.lambdaQuery();
        queryChainWrapper.eq(Objects.nonNull(labelDataRefQuery.getDataIdentifier()), LabelDataRefPo::getDataIdentifier, labelDataRefQuery.getDataIdentifier())
                .eq(Objects.nonNull(labelDataRefQuery.getLabelId()), LabelDataRefPo::getLabelId, labelDataRefQuery.getLabelId())
                .eq(StringUtil.isNotBlank(labelDataRefQuery.getAuthCode()), LabelDataRefPo::getAuthCode, labelDataRefQuery.getAuthCode())
                .eq(StringUtil.isNotBlank(labelDataRefQuery.getDataType()), LabelDataRefPo::getDataType, labelDataRefQuery.getDataType())
                .like(StringUtil.isNotBlank(labelDataRefQuery.getLabelName()), LabelDataRefPo::getLabelName, labelDataRefQuery.getLabelName())
                .like(StringUtil.isNotBlank(labelDataRefQuery.getLabelTypeName()), LabelDataRefPo::getLabelTypeName, labelDataRefQuery.getLabelTypeName())
                .in(CollectionUtil.isNotEmpty(labelDataRefQuery.getDataIdentifierList()), LabelDataRefPo::getDataIdentifier, labelDataRefQuery.getDataIdentifierList());

        if (CollectionUtil.isNotEmpty(labelDataRefQuery.getAccessScope())) {
            for (String acceptScope : labelDataRefQuery.getAccessScope()) {
                queryChainWrapper.apply(StringUtil.isNotBlank(acceptScope), "access_scope ->'$[*]' like concat('%',{0},'%')", acceptScope);
            }
        }
        List<LabelDataRefPo> poList = queryChainWrapper.list();
        return KcBeanConverter.toList(poList, LabelDataRefDto.class);
    }

    @Override
    public List<LabelInfoAvailableDto> availableList(@Valid LabelInfoAvailableQuery labelInfoAvailableQuery) {
        List<LabelInfoAvailableDto> availableList = labelInfoService.queryAvailableLabelInfoList(labelInfoAvailableQuery).getResult();
        if (CollectionUtil.isEmpty(availableList) || StringUtil.isBlank(labelInfoAvailableQuery.getDataIdentifier())) {
            return availableList;
        }

        LabelDataRefQuery labelDataRefQuery = KcBeanConverter.toBean(labelInfoAvailableQuery, LabelDataRefQuery.class);
        labelDataRefQuery.setAccessScope(Collections.singletonList(labelInfoAvailableQuery.getUserIdentity()));
        labelDataRefQuery.setDataTypeList(labelInfoAvailableQuery.getLabelTargetType());
        List<LabelDataRefDto> labelDataRefDtoList = this.listRelatedLabel(labelDataRefQuery);
        if (labelDataRefDtoList.isEmpty()) {
            return availableList;
        }
        List<Long> labelIdList = labelDataRefDtoList.stream().map(LabelDataRefDto::getLabelId).collect(Collectors.toList());
        return availableList.stream().filter(labelInfoAvailableDto -> !labelIdList.contains(labelInfoAvailableDto.getLabelId())).collect(Collectors.toList());
    }

    /**
     * 校验打标入参、command -> po 转换
     */
    private List<LabelDataRefPo> generateLabelDataRefPos(LabelDataRefSaveCommand labelDataRefSaveCommand) {
        // 业务维度 参数校验
        CheckNodeChainBuilder.<LabelDataRefSaveCommand>builder()
                .appendCheckNode(command -> {
                    List<Long> distinctLabelIds = command.getLabelInfo().stream().map(LabelDataRefSaveCommand.LabelInfoSimple::getLabelId).distinct().collect(Collectors.toList());
                    if (distinctLabelIds.size() != command.getLabelInfo().size()) {
                        log.error("禁止添加重复标签，入参：{}", command);
                        throw new LabelMarkForbiddenException("禁止添加重复标签！");
                    }
                })
                .appendCheckNode(UserIdentityCheckNode::new)
                .appendCheckNode(ShipperCooperationSaveCheckNode::new)
                .appendCheckNodeBean(SameLabelDataRefCheckNode.class)
                .appendCheckNodeBean(DataLabelLimitCheckNode.class)
                .invokeAll(labelDataRefSaveCommand);
        // TODO: 2023/4/10 是否有必要 加校验，当前标签的标签对象 与 dataType字段 是否匹配
        // TODO: 2023/4/11 是否有必要 加校验, 标签是否启用中

        List<LabelDataRefPo> labelDataRefPoList = new ArrayList<>();
        for (LabelDataRefSaveCommand.LabelInfoSimple labelInfo : labelDataRefSaveCommand.getLabelInfo()) {
            LabelDataRefPo labelDataRefPo = LabelDataRefConverter.INSTANCE.COMMAND2PO(labelDataRefSaveCommand);
            labelDataRefPo.setLabelId(labelInfo.getLabelId());
            labelDataRefPo.setLabelName(labelInfo.getLabelName());
            labelDataRefPo.setLabelTypeName(labelInfo.getLabelTypeName());
            labelDataRefPoList.add(labelDataRefPo);
        }
        return labelDataRefPoList;
    }

}
