package com.kcwl.common.log.util;

import com.kcwl.tenant.datasource.MultiDataSourceContextHolder;

/**
 * 多数据源帮助
 *
 * @see MultiDataSourceContextHolder
 */
public class MultiDataSourceHelper {

    /**
     * 手动切换数据源，执行，并切换回来
     */
    public static void switchAndRun(String dataSourceName, Runnable runnable) {
        String oldDataSourceName = MultiDataSourceContextHolder.getDataSourceName();
        try {
            MultiDataSourceContextHolder.setDataSourceName(dataSourceName);
            runnable.run();
        } finally {
            if (oldDataSourceName == null) {
                MultiDataSourceContextHolder.clearDataSourceType();
            } else {
                MultiDataSourceContextHolder.setDataSourceName(oldDataSourceName);
            }
        }
    }

}
