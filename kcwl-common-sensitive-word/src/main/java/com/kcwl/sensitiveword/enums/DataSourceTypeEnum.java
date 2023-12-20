package com.kcwl.sensitiveword.enums;

/**
 * <p>
 * 词库数据源类型枚举
 * </p>
 *
 * @author renyp
 * @since 2023/5/26 10:42
 */
public enum DataSourceTypeEnum {

    /**
     * 文件
     */
    FILE,
    /**
     * mybatis
     */
    MYBATIS,
    /**
     * 远程词库
     */
    REMOTE,
    /**
     * 当前项目接入的全部词库
     */
    MIXED;
}
