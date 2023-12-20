package com.kcwl.sensitiveword.datasource;

import com.kcwl.ddd.interfaces.dto.PageInfoDTO;
import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import com.kcwl.sensitiveword.enums.SensitiveWordTypeEnum;
import com.kcwl.sensitiveword.pojo.command.SensitiveWordInfoCommand;
import com.kcwl.sensitiveword.pojo.dto.SensitiveWordInfoDto;
import com.kcwl.sensitiveword.pojo.po.SensitiveWordInfoSlim;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 多类型 敏感词 数据源
 * </p>
 *
 * @author renyp
 * @since 2023/5/26 14:54
 */

@RequiredArgsConstructor
@Slf4j
public class MixedDataSourceAdapter implements DataSourceAdapter {

    private final List<DataSourceAdapter> dataSourceAdapters;

    @Override
    public List<SensitiveWordInfoSlim> reloadSensitiveWord() {
        final List<SensitiveWordInfoSlim> memorySensitiveWordRepository = new ArrayList<>();
        for (DataSourceAdapter dataSourceAdapter : dataSourceAdapters) {
            memorySensitiveWordRepository.addAll(dataSourceAdapter.reloadSensitiveWord());
        }
        return memorySensitiveWordRepository;
    }

    @Override
    public PageInfoDTO<SensitiveWordInfoDto> querySensitiveWordPage(SensitiveWordTypeEnum sensitiveWordType, SensitiveLevelEnum sensitiveWordLevel) {
        // TODO: 2023/5/26  
        return null;
    }

    @Override
    public boolean saveSensitiveWordAndRefresh(SensitiveWordInfoCommand sensitiveWordInfoCommand) {
        boolean flag = true;
        for (DataSourceAdapter dataSourceAdapter : dataSourceAdapters) {
            if (!dataSourceAdapter.saveSensitiveWordAndRefresh(sensitiveWordInfoCommand)) {
                log.error("敏感词库 {}，新增敏感词 {} 失败！", dataSourceAdapter.getClass().getName(), sensitiveWordInfoCommand);
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean saveBatchSensitiveWordAndRefresh(List<SensitiveWordInfoCommand> sensitiveWordInfoCommands) {
        boolean flag = true;
        for (DataSourceAdapter dataSourceAdapter : dataSourceAdapters) {
            if (!dataSourceAdapter.saveBatchSensitiveWordAndRefresh(sensitiveWordInfoCommands)) {
                log.error("敏感词库 {}，批量新增敏感词 {} 失败！", dataSourceAdapter.getClass().getName(), sensitiveWordInfoCommands);
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean removeSensitiveWordAndRefresh(Long id) {
        boolean flag = true;
        for (DataSourceAdapter dataSourceAdapter : dataSourceAdapters) {
            if (!dataSourceAdapter.removeSensitiveWordAndRefresh(id)) {
                log.error("敏感词库 {}，删除敏感词 {} 失败！", dataSourceAdapter.getClass().getName(), id);
                flag = false;
            }
        }
        return flag;
    }
}
