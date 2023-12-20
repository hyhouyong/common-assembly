package com.kcwl.sensitiveword.datasource;

import com.kcwl.ddd.interfaces.dto.PageInfoDTO;
import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import com.kcwl.sensitiveword.enums.SensitiveWordTypeEnum;
import com.kcwl.sensitiveword.pojo.command.SensitiveWordInfoCommand;
import com.kcwl.sensitiveword.pojo.dto.SensitiveWordInfoDto;
import com.kcwl.sensitiveword.pojo.po.SensitiveWordInfoSlim;

import java.util.List;

/**
 * <p>
 * 敏感词数据源适配器
 * </p>
 *
 * @author renyp
 * @since 2023/5/26 10:33
 */
public interface DataSourceAdapter {


    /**
     * 加载敏感词库
     *
     * @return 加载结果
     */
    List<SensitiveWordInfoSlim> reloadSensitiveWord();

    /**
     * 分页查询敏感词
     *
     * @param sensitiveWordType  敏感词类型
     * @param sensitiveWordLevel 敏感级别
     * @return 敏感词
     */
    PageInfoDTO<SensitiveWordInfoDto> querySensitiveWordPage(SensitiveWordTypeEnum sensitiveWordType, SensitiveLevelEnum sensitiveWordLevel);

    /**
     * 添加敏感词 并重载词库
     *
     * @param sensitiveWordInfoCommand 敏感词信息
     * @return 添加并重载 结果
     */
    boolean saveSensitiveWordAndRefresh(SensitiveWordInfoCommand sensitiveWordInfoCommand);

    /**
     * 批量添加敏感词 并重载词库
     *
     * @param sensitiveWordInfoCommands 敏感词信息
     * @return 批量添加并重载 结果
     */
    boolean saveBatchSensitiveWordAndRefresh(List<SensitiveWordInfoCommand> sensitiveWordInfoCommands);

    /**
     * 移除敏感词并重载
     *
     * @param id 敏感词id
     * @return 移除并重载 结果
     */
    boolean removeSensitiveWordAndRefresh(Long id);


}
