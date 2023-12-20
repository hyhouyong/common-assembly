package com.kcwl.common.log.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.util.Map;

/**
 * Spring Http 工具类
 */
public class HttpClientUtil {

    /**
     * 获取请求头
     *
     * @param request 对外请求的 request，比如 RestTemplate 中
     */
    public static String getRequestHeaderJson(HttpRequest request) {
        try {
            Map<String, String> headerMap = request.getHeaders().toSingleValueMap();
            if (CollUtil.isNotEmpty(headerMap)) {
                return new JSONObject(headerMap).toString();
            }
        } catch (Exception ignored) {
        }
        return "{}";
    }

    public static boolean isGetMethod(HttpRequest request) {
        return "GET".equalsIgnoreCase(request.getMethodValue());
    }

    public static boolean isPostMethod(HttpRequest request) {
        return "POST".equalsIgnoreCase(request.getMethodValue());
    }

    /**
     * form body 参数 转 JSON
     * "form1=form1value&form2=form2Value"
     */
    // TODO: 2021/8/25 参数值中包含 = 情况
    public static String formBody2Json(String formBody) {
        if (StrUtil.isBlank(formBody)) {
            return "{}";
        }
        try {
            String[] params = formBody.split("&");
            if (params.length > 0) {
                JSONObject json = new JSONObject();
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        json.set(keyValue[0], keyValue[1]);
                    }
                }
                return json.toString();
            }
        } catch (Exception ignored) {
        }
        return "{}";
    }

    /**
     * 确保 response content-type 为 application/json
     * 且 getBody() 可以多次获取
     */
    public static JSONObject getResponseJson(ClientHttpResponse response) {
        try {
            return new JSONObject(IoUtil.read(response.getBody()).toString());
        } catch (Exception ignored) {
        }
        return new JSONObject();
    }

}
