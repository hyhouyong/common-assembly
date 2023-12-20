package com.kcwl.sensitiveword.service;

import com.kcwl.sensitiveword.datasource.DataSourceAdapter;
import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import com.kcwl.sensitiveword.pojo.dto.FoundWordInfoDto;
import com.kcwl.sensitiveword.pojo.po.SensitiveWordInfoSlim;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * <p>
 * 敏感词搜索 相关api
 * </p>
 *
 * @author renyp
 * @since 2023/5/26 10:32
 */
@RequiredArgsConstructor
public abstract class SensitiveWordScanService {

    private final DataSourceAdapter currentDataSourceAdapter;

    /**
     * 加载全部 敏感词
     *
     * @return 敏感词集合
     */
    protected List<SensitiveWordInfoSlim> loadAllSensitiveWord() {
        return currentDataSourceAdapter.reloadSensitiveWord();
    }

    /**
     * 初始化词树
     *
     * @return 初始化结果
     */
    public abstract boolean initializeWordTree();


    /**
     * 是否存在敏感词
     *
     * @param text               母串文本
     * @param sensitiveWordLevel 敏感级别
     * @return 匹配结果
     */
    public abstract boolean existsSensitiveWord(String text, SensitiveLevelEnum sensitiveWordLevel);

    /**
     * 查找敏感词 并返回第一个匹配到的敏感词
     *
     * @param text           母串文本
     * @param isDensityMatch 是否使用密集匹配模式
     * @return 查找到的第一个敏感词（若无，返回一个 属性为空的 FoundWordInfoDto 实例）
     */
    public abstract FoundWordInfoDto searchSensitiveWord(String text, boolean isDensityMatch);

    /**
     * 查找 并 返回命中的全部 敏感词
     *
     * @param text           母串文本
     * @param isDensityMatch 是否使用密集匹配模式
     * @return 命中的全部敏感词，若匹配失败，返回空的List
     */
    public abstract List<FoundWordInfoDto> searchAllSensitiveWord(String text, boolean isDensityMatch);

//    /**
//     * 敏感词分页查询
//     *
//     * @param dataSourceType     数据源类型
//     * @param sensitiveWordLevel 敏感级别
//     * @param sensitiveWordType  敏感类型
//     * @return 敏感词分页查询结果
//     */
//    public abstract PageInfoDTO<SensitiveWordInfoDto> querySensitiveWordPage(DataSourceTypeEnum dataSourceType, SensitiveWordLevelEnum sensitiveWordLevel, SensitiveWordTypeEnum sensitiveWordType);
//
//    /**
//     * 保存敏感词
//     *
//     * @param sensitiveWordInfoCommand 敏感词信息
//     * @return 保存结果
//     */
//    public abstract boolean saveSensitiveWordAndRefresh(SensitiveWordInfoCommand sensitiveWordInfoCommand);
//
//    /**
//     * 批量保存敏感词
//     *
//     * @param sensitiveWordInfoCommands 敏感词信息
//     * @return 批量保存结果
//     */
//    public abstract boolean saveBatchSensitiveWordAndRefresh(List<SensitiveWordInfoCommand> sensitiveWordInfoCommands);
//
//    /**
//     * 删除敏感词
//     *
//     * @param id 敏感词标识
//     * @return 删除结果
//     */
//    public abstract boolean removeSensitiveWordAndRefresh(Long id);


}
