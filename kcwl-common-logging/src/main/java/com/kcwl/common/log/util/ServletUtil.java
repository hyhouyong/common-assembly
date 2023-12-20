package com.kcwl.common.log.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.ImmutableSet;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class ServletUtil extends cn.hutool.extra.servlet.ServletUtil {

    /**
     * 获取 HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 如果 ServletRequest 为缓存包装类 获取 Body
     */
    public static String getCachedRequestBody(HttpServletRequest request) {
        try {
            if (request instanceof com.kcwl.framework.rest.web.filter.reqeust.ContentCachingRequestWrapper
                    || request instanceof org.springframework.web.util.ContentCachingRequestWrapper) {
                return getBody(request);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 获取所有请求参数 JSON
     * 会返回 null
     */
    public static String getParamMapJson(HttpServletRequest request) {
        try {
            Map<String, String> paramMap = getParamMap(request);
            if (CollUtil.isNotEmpty(paramMap)) {
                return new JSONObject(paramMap).toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    // 获取请求头
    public static String getRequestHeaderJson(HttpServletRequest request) {
        try {
            Map<String, String> headerMap = getHeaderMap(request);
            if (CollUtil.isNotEmpty(headerMap)) {
                return new JSONObject(headerMap).toString();
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    // 从 JSON 中添加请求头
    public static void addRequestHeaderFromJson(MultiValueMap<String, String> multiValueMap, String requestHeaderJson) {
        if (multiValueMap == null) {
            return;
        }
        try {
            new JSONObject(requestHeaderJson).forEach((k, v) -> {
                if (shouldReuse4Request(k)) {
                    multiValueMap.add(k, String.valueOf(v));
                }
            });
        } catch (Exception ignored) {
        }
    }

    /**
     * 根据 JSON 字符串，获取 key1=value1&key2=value2... 格式
     * 不包含 `?` 前缀
     */
    public static String getRequestParamSuffixFromJson(String json) {
        try {
            LinkedList<String> list = new LinkedList<>();
            new JSONObject(json).forEach((k, v) -> list.add(k + "=" + v));
            if (CollUtil.isNotEmpty(list)) {
                return CollUtil.join(list, "&");
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    // 不应该复用的头集合
    private final static Set<String> ONE_TIME_HEADER_SET = new ImmutableSet.Builder<String>()
            .add("appsecret")
            .add("host")
            .add("sw8", "sw8-correlation", "sw8-x") // skywalking 参数
            .add("x-agent-client", "user-agent")
            .add("transfer-encoding", "accept-encoding")
            .add("connection")
            .add("cache-control")
            .add("upgrade-insecure-requests")
            .add("sec-fetch-mode", "sec-fetch-site", "sec-fetch-dest", "sec-fetch-user", "sec-ch-ua-mobile")
            .build();

    /**
     * 是否应该重新使用
     * 部分 header 比如 content-length，是请求前重新生成的，应该是动态改变的
     */
    public static boolean shouldReuse4Request(String param) {
        if (StrUtil.isBlank(param)) {
            return false;
        }
        return !ONE_TIME_HEADER_SET.contains(param.toLowerCase());
    }

    // 根据 code 判断 ResponseMessage 是否成功
    public static boolean isResponseMessageSuccess(String code) {
        return StrUtil.isNotBlank(code)
                && ("0".equals(code) || code.endsWith("200"));

    }


}
