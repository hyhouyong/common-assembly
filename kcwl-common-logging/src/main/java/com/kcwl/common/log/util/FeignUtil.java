package com.kcwl.common.log.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import feign.Request;
import feign.Response;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * feign 工具类
 */
public class FeignUtil {

    // 获取 请求 ContentType
    public static String getContentType(Request request) {
        return getContentType(request.headers());
    }

    // 获取响应 ContentType
    public static String getContentType(Response response) {
        return getContentType(response.headers());
    }

    // 获取 ContentTypeContentType
    public static String getContentType(Map<String, Collection<String>> headers) {
        if (CollUtil.isEmpty(headers)) {
            return null;
        }

        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(entry.getKey())
                    && CollUtil.isNotEmpty(entry.getValue())) {
                return CollUtil.get(entry.getValue(), 0); // 获取首次出现
            }
        }
        return null;
    }

    // feign.Response 返回值包含任意 value
    public static boolean contentTypeContainsAny(Response response, String... values) {
        if (response == null || values == null || values.length == 0) {
            return false;
        }
        return StrUtil.equalsAny(getContentType(response), values);
    }

    // 获取请求 body
    public static String getBodyJsonString(Request request) {
        try {
            return new String(request.body());
        } catch (Exception ignored) {
        }
        return "{}";
    }

    // 获取 response body json object
    public static JSONObject getBodyJsonObject(Response response) {
        try {
            // TODO: 2021/8/26 不能 repeatable 问题，相当于没法解析 Response
            //  自定义一个 ByteArrayBody
            if (response.body().isRepeatable()) {
                return new JSONObject(IoUtil.read(response.body().asReader()));
            }
        } catch (Exception ignored) {
        }
        return new JSONObject();
    }

    // 解析请求 url
    public static Optional<URI> parseUrl(Request request) {
        try {
            return Optional.of(new URI(request.url()));
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    // 获取对外请求 header json
    public static String getRequestHeaderJson(Request request) {
        try {
            Map<String, Collection<String>> map = request.headers();
            if (CollUtil.isNotEmpty(map)) {
                JSONObject jsonObject = new JSONObject();
                map.forEach((key, values) -> {
                    if (CollUtil.isNotEmpty(values)) {
                        jsonObject.set(key, CollUtil.get(values, 0));
                    }
                });
                return jsonObject.toString();
            }
        } catch (Exception e) {
        }
        return "{}";
    }

    public static boolean isGetMethod(Request request) {
        return request.httpMethod() == Request.HttpMethod.GET;
    }

    public static boolean isPostMethod(Request request) {
        return request.httpMethod() == Request.HttpMethod.POST;
    }

    // 表单 body 转 json
    public static String formBody2Json(Request request) {
        try {
            return HttpClientUtil.formBody2Json(new String(request.body()));
        } catch (Exception ignored) {
        }
        return "{}";
    }

}
