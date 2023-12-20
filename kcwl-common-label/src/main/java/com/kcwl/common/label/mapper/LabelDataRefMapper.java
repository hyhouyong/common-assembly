package com.kcwl.common.label.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kcwl.common.label.pojo.po.LabelDataRefPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 数据与标签关联表 Mapper 接口
 * </p>
 *
 * @author renyp
 * @since 2023-03-29
 */

@Mapper
public interface LabelDataRefMapper extends BaseMapper<LabelDataRefPo> {

}
