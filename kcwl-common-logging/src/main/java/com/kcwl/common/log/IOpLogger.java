package com.kcwl.common.log;

import com.kcwl.common.log.pojo.LogJoinPointContext;

/**
 * @author ckwl
 */
public interface IOpLogger {
    /**
     * 记录操作日志
     * @param logJoinPointInfo
     */
    public void record(final LogJoinPointContext logJoinPointInfo);
}
