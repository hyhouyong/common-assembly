package com.kcwl.common.risk.util;

/**
 * 缓存工具类
 * @author ckwl
 */
public class RedisKeyUtil {

    private static final String LOGIN_PASSWORD_ERROR_COUNT = "login:password:error:count:";

    private static final String LOGIN_PASSWORD_ERROR_TIMESTAMP = "login:password:error:Timestamp:";

    private static final String LOGIN_KC_TOKEN_USER_NOT_EXIST_COUNT = "login:kcToken:userNotExist:count:";

    private static final String LOGIN_IP_USER_NOT_EXIST_COUNT = "login:ip:userNotExist:count:";

    /**
     * 获取密码登录错误次数key(iuc使用)
     * @param userId
     * @param appId
     * @param platform
     * @return
     */
    public static String getLoginPasswordErrorCount(String platform,String appId,Long userId) {
        return LOGIN_PASSWORD_ERROR_COUNT + platform + appId + userId;
    }

    /**
     * 获取密码登录错误到达锁定阈值时间戳key(iuc使用)
     * @param userId
     * @param appId
     * @param platform
     * @return
     */
    public static String getLoginPasswordErrorTimestamp(String platform,String appId,Long userId) {
        return LOGIN_PASSWORD_ERROR_TIMESTAMP + platform + appId + userId;
    }


    /**
     * 获取密码登录错误次数key（euc使用）
     * @param mobile
     * @return
     */
    public static String getLoginPasswordErrorCount(String mobile) {
        return LOGIN_PASSWORD_ERROR_COUNT + mobile;
    }

    /**
     * 获取密码登录错误到达锁定阈值时间戳key（euc使用）
     * @param mobile
     * @return
     */
    public static String getLoginPasswordErrorTimestamp(String mobile) {
        return LOGIN_PASSWORD_ERROR_TIMESTAMP + mobile;
    }


    /**
     * 获取登录时kcToken 手机号不存在的次数key
     * @param kcToken
     * @return
     */
    public static String getLoginKcTokenUserNotExistCount(String kcToken) {
        return LOGIN_KC_TOKEN_USER_NOT_EXIST_COUNT + kcToken;
    }

    /**
     * 获取登录时ip 手机号不存在的次数key
     * @param ip
     * @return
     */
    public static String getLoginIpUserNotExistCount(String ip) {
        return LOGIN_IP_USER_NOT_EXIST_COUNT + ip;
    }


}
