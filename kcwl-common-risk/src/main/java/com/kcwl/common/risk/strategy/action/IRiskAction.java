package com.kcwl.common.risk.strategy.action;

/**
 * 风险处理
 * @author ckwl
 */
public interface IRiskAction {
    /**
     * 风险处理方法
     * @param param 处置参数（数据库中配置的）
     * @param actionArgs 处置参数（外部传参）
     */
    void dispose(String param,Object actionArgs);
}
