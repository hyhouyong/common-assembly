package com.kcwl.common.risk.loader;

import cn.hutool.extra.spring.SpringUtil;
import com.kcwl.common.risk.entity.UserRiskPolicy;
import com.kcwl.common.risk.enums.RiskLevel;
import com.kcwl.common.risk.exception.UserRiskException;
import com.kcwl.common.risk.strategy.action.IRiskAction;
import com.kcwl.common.risk.strategy.risk.IUserRisk;
import com.kcwl.ddd.infrastructure.constants.EmptyObject;
import com.kcwl.framework.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 风险策略执行器
 * @author ckwl
 */
@Slf4j
@Service
public class UserRiskCheckService {

    @Resource
    Map<String, IUserRisk> userRiskMap;
    @Resource
    Map<String, IRiskAction> riskActionMap;

    public void doRiskCheck(String eventType, Object args){
        List<UserRiskPolicy> userRiskPolicyList = getUserRiskPolicyList(eventType);
        if(CollectionUtil.isEmpty(userRiskPolicyList)){
            return;
        }
        Collections.sort(userRiskPolicyList);
        userRiskPolicyList.forEach(riskPolicy -> {
            IUserRisk userRisk = userRiskMap.get(riskPolicy.getRiskName());
            if ( userRisk != null ) {
                RiskLevel riskLevel = RiskLevel.RISK_NONE;
                try{
                    riskLevel = userRisk.check(args, riskPolicy.getRiskFactor());
                } catch(Exception e){
                    log.warn("risk check execute error,risk name:[{}], exception msg:[{}]", userRisk.getClass().getName(), e.getCause());
                }
                if ( riskLevel.greaterThan(riskPolicy.getActionLevel()) ) {
                    try{
                        onRiskAction(riskPolicy,args);
                    } catch(Exception e){
                        log.warn("risk action execute error,risk name:[{}], exception msg:[{}]", userRisk.getClass().getName(), e.getCause());
                        if(e instanceof UserRiskException){
                            throw e;
                        }
                    }
                }
            }
        });
    }

    private void onRiskAction(UserRiskPolicy riskPolicy,Object actionArgs){
        IRiskAction riskAction = riskActionMap.get(riskPolicy.getRiskAction());
        if ( riskAction != null ){
            riskAction.dispose(riskPolicy.getActionParam(),actionArgs);
        } else {
            log.warn("can not find risk action[{}]", riskPolicy.getRiskAction());
        }
    }

    private List<UserRiskPolicy> getUserRiskPolicyList(String eventType){
        IUserRiskPolicyLoader userRiskPolicyLoader = SpringUtil.getBean(IUserRiskPolicyLoader.class);
        return (userRiskPolicyLoader != null) ? userRiskPolicyLoader.getUserRiskPolicyList(eventType) : EmptyObject.LIST;
    }
}
