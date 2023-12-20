package com.kcwl.common.label.service.converter;

import com.kcwl.common.label.pojo.po.LabelDataRefPo;
import com.kcwl.ddd.domain.entity.UserAgent;
import com.kcwl.ddd.infrastructure.session.SessionContext;
import com.kcwl.ddd.infrastructure.session.SessionData;
import com.kcwl.support.label.command.LabelDataRefSaveBatchCommand;
import com.kcwl.support.label.command.LabelDataRefSaveCommand;
import com.kcwl.support.label.constants.LabelRelatedConstant;
import com.kcwl.support.label.dto.LabelDataRefDto;
import com.kcwl.support.label.enums.IdentityRelatedLabelEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

/**
 * <p>
 * 标签 与 数据关联关系 转换器
 * </p>
 *
 * @author renyp
 * @since 2023/4/4 15:32
 */

@Mapper(imports = {Optional.class, SessionContext.class, SessionData.class, UserAgent.class, IdentityRelatedLabelEnum.class, LabelRelatedConstant.class})
public interface LabelDataRefConverter {

    LabelDataRefConverter INSTANCE = Mappers.getMapper(LabelDataRefConverter.class);

    @Mapping(target = "product", source = "product", defaultExpression = "java(Optional.ofNullable(SessionContext.getRequestUserAgent()).map(session -> session.getProduct()).orElse(\"{product}\"))")
    @Mapping(target = "platformNo", source = "platformNo", defaultExpression = "java(Optional.ofNullable(SessionContext.getRequestUserAgent()).map(session -> session.getPlatform()).orElse(\"{platform}\"))")
    @Mapping(target = "authCode", expression = "java(IdentityRelatedLabelEnum.PLATFORM.getCode().equals(labelDataRefSaveCommand.getCreatorIdentity()) ? LabelRelatedConstant.PLATFORM_DEFAULT_AUTH_CODE : labelDataRefSaveCommand.getAuthCode())")
    LabelDataRefPo COMMAND2PO(LabelDataRefSaveCommand labelDataRefSaveCommand);

    LabelDataRefDto PO2DTO(LabelDataRefPo labelDataRefPo);

    LabelDataRefSaveCommand batchCommand2Command(LabelDataRefSaveBatchCommand labelDataRefSaveBatchCommand);

}
