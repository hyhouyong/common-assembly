package com.kcwl.sensitiveword.provider;

import cn.hutool.core.lang.Console;
import cn.hutool.dfa.FoundWord;
import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import com.kcwl.sensitiveword.mapstructs.FoundWordMapper;
import com.kcwl.sensitiveword.service.SensitiveWordScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * <p>
 * 基于NFA实现的AC自动机
 * </p>
 *
 * @author renyp
 * @since 2023/5/26 10:16
 */

@Slf4j
@RequiredArgsConstructor
public class AutomatonNfaSensitiveWordScanProvider implements SensitiveWordScanProvider {

    private final SensitiveWordScanService sensitiveWordScanService;


    @PostConstruct
    void initAutomaton() {
        Console.print("  =================================================================================================================\n");
        Console.print("             |\\    |    ------         -                                     -                --- \n");
        Console.print("             | \\   |     |            / \\                                   / \\             (    \n");
        Console.print("             |  \\  |     |----       /---\\                                 /---\\           (      \n");
        Console.print("             |   \\ |     |          /     \\                               /     \\           (    \n");
        Console.print("             |    \\|     |         /       \\                             /       \\            ---\n");
        Console.print("  =================================================================================================================\n");
        // 2023/5/29 初始化字典树 并构建AC自动机
        if (this.sensitiveWordScanService.initializeWordTree()) {
            log.info("--------------------------- Initialize  trie(Automaton-NFA)  success! -----------------------------");
        } else {
            log.error("--------------------------- Initialize  trie(Automaton-NFA)  failed! -----------------------------");
        }
    }

    @Override
    public boolean existsSensitiveWord(String text) {
        return this.existsSensitiveWord(text, SensitiveLevelEnum.NORMAL);
    }


    @Override
    public boolean existsSensitiveWord(String text, SensitiveLevelEnum sensitiveWordLevel) {
        return this.sensitiveWordScanService.existsSensitiveWord(text, sensitiveWordLevel);
    }

    @Override
    public FoundWord searchSensitiveWord(String text) {
        return FoundWordMapper.INSTANCE.foundWord2Dto(this.sensitiveWordScanService.searchSensitiveWord(text, false));
    }

    @Override
    public List<FoundWord> searchAllSensitiveWord(String text) {
        return this.searchAllSensitiveWord(text, false);
    }

    @Override
    public List<FoundWord> searchAllSensitiveWord(String text, boolean isDensityMatch) {
        return FoundWordMapper.INSTANCE.foundWord2DtoList(this.sensitiveWordScanService.searchAllSensitiveWord(text, isDensityMatch));
    }


}
