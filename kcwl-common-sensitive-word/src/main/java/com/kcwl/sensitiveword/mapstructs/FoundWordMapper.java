package com.kcwl.sensitiveword.mapstructs;


import cn.hutool.dfa.FoundWord;
import com.kcwl.sensitiveword.pojo.dto.FoundWordInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * <p>
 * FoundWord FoundWordInfoDto 转换器
 * </p>
 *
 * @author renyp
 * @since 2023/5/31 16:09
 */
@Mapper
public interface FoundWordMapper {

    FoundWordMapper INSTANCE = Mappers.getMapper(FoundWordMapper.class);

    FoundWord foundWord2Dto(FoundWordInfoDto foundWordInfoDto);

    List<FoundWord> foundWord2DtoList(List<FoundWordInfoDto> foundWordInfoDto);
}
