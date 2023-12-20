package com.kcwl.app.response;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.kcwl.aid.interfaces.api.ErrorPromptService;
import com.kcwl.aid.interfaces.dto.ErrorPromptDTO;
import com.kcwl.aid.interfaces.query.ErrorPromptQuery;
import com.kcwl.ddd.infrastructure.api.IErrorPromptDecorator;
import com.kcwl.ddd.infrastructure.api.ResponseMessage;
import com.kcwl.ddd.interfaces.dto.PageInfoDTO;
import com.kcwl.ddd.interfaces.dto.PageResultDTO;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 错误码 提示语 热更新处理器
 * </p>
 *
 * @author renyp
 * @since 2023/2/28 20:36
 */

@Slf4j
public class DefaultErrorPromptDecorator implements IErrorPromptDecorator {

    private final ErrorPromptService errorPromptService;

    private final LoadingCache<Pair<String, String>, String> errorPromptCache;
    /**
     * 错误码 类型 编码
     */
    public static final Integer ERROR_CODE = 1;
    /**
     * 页面提示 类型 编码
     */
    public static final Integer PAGE_CODE = 2;

    /**
     * 错误码 状态 启用
     */
    public static final Integer CODE_STATUS_ENABLE = 1;
    /**
     * 错误码 状态 停用
     */
    public static final Integer CODE_STATUS_DISABLE = 0;


    @Override
    public String getErrorPrompt(String code, String product) {
        StopWatch timeConsuming = new StopWatch();
        try {
            timeConsuming.start("error-prompt-query-time-statistic");
            return errorPromptCache.get(new Pair<>(code, product));
        } finally {
            try {
                timeConsuming.stop();
            } catch (Exception ex) {
                log.error("error-prompt stop-watch was terminated failed! ", ex);
            }
            log.debug(timeConsuming.prettyPrint());
        }
    }

    public DefaultErrorPromptDecorator(ErrorPromptService errorPromptService) {
        this.errorPromptService = errorPromptService;
        this.errorPromptCache = Caffeine.newBuilder()
                .expireAfterWrite(10L, TimeUnit.MINUTES)
                .maximumSize(1_000)
                .build(new CacheLoader<Pair<String, String>, String>() {
                    @Override
                    public @Nullable String load(Pair<String, String> key) throws Exception {
                        try {
                            ErrorPromptQuery errorPromptQuery = new ErrorPromptQuery();
                            errorPromptQuery.setCodeIdentifier(key.getKey());
                            errorPromptQuery.setCodeType(ERROR_CODE);
                            errorPromptQuery.setProductCode(Integer.valueOf(key.getValue()));
                            errorPromptQuery.setCodeStatus(CODE_STATUS_ENABLE);

                            return Optional.ofNullable(errorPromptService.errorPromptQuery(errorPromptQuery))
                                    .map(ResponseMessage::getResult)
                                    .map(PageResultDTO::getPage)
                                    .map(PageInfoDTO::getList)
                                    .map(errorPromptDTOS -> errorPromptDTOS.stream().findAny().orElseGet(ErrorPromptDTO::new))
                                    .map(ErrorPromptDTO::getPromptContent)
                                    .orElse(StrUtil.EMPTY);
                        } catch (Exception exception) {
                            log.error("RPC查询错误码提示语异常，入参 code:{}, product:{}", key.getKey(), key.getValue(), exception);
                            return StrUtil.EMPTY;
                        }
                    }
                });
    }
}
