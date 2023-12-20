package com.kcwl.sensitiveword.provider;

import cn.hutool.core.date.StopWatch;
import cn.hutool.dfa.FoundWord;
import com.kcwl.sensitiveword.configuration.SensitiveWordAutoConfiguration;
import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * ac自动机 敏感词 搜索工具 单测类
 * </p>
 *
 * @author renyp
 * @since 2023/5/30 11:19
 */

@SpringBootTest(classes = {SensitiveWordAutoConfiguration.class, AutomatonNfaSensitiveWordScannerProviderTest.class}, properties = "kcwl.common.web.sensitiveWord.enable=true")
@RunWith(SpringRunner.class)
public class AutomatonNfaSensitiveWordScannerProviderTest {

    @Autowired
    private SensitiveWordScanProvider sensitiveWordScanProvider;

    private StopWatch stopWatch;

    @Before
    public void init() {
        stopWatch = new StopWatch();
    }

    @Test
    public void existsSensitiveWord() {
        stopWatch.start("ExistsSensitiveWord(String):boolean Timer");
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本应该没有敏感词"));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 Q 不是敏感词"));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 00wyt.com 不是敏感词"));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 出售 不是敏感词"));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 动画 不是敏感词"));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 援助交 不是敏感词"));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 淘宝 是敏感词"));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 QQ 是敏感词"));

        Assert.assertTrue(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 弓弩麻醉镖 是敏感词"));
        Assert.assertTrue(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 军刀电棍销售 是敏感词"));
        Assert.assertTrue(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 h动画 是敏感词"));
        Assert.assertTrue(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 援助交际 是敏感词"));
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.NANOSECONDS));
    }

    /**
     * 不同敏感级别匹配 单测
     */
    @Test
    public void testExistsSensitiveWord() {
        stopWatch.start("ExistsSensitiveWord(String, SensitiveWordLevel):boolean Timer");

        Assert.assertTrue(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 弓弩麻醉镖 是敏感词", SensitiveLevelEnum.STRICT));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 QQ 不是敏感词", SensitiveLevelEnum.NORMAL));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 弓弩麻醉镖 是敏感词, 但是用了 弱敏感级别，不能匹配这个normal级别的敏感词", SensitiveLevelEnum.WEAK));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 军刀电棍销售 是敏感词, 但是用了 弱敏感级别，不能匹配这个normal级别的敏感词", SensitiveLevelEnum.WEAK));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 h动画 是敏感词, 但是用了 弱敏感级别，不能匹配这个normal级别的敏感词", SensitiveLevelEnum.WEAK));
        Assert.assertFalse(sensitiveWordScanProvider.existsSensitiveWord("这串文本里 援助交际 是敏感词, 但是用了 弱敏感级别，不能匹配这个normal级别的敏感词", SensitiveLevelEnum.WEAK));
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.NANOSECONDS));
    }

    @Test
    public void searchSensitiveWord() {
        stopWatch.start("SearchSensitiveWord(String):FoundWord Timer");

        FoundWord foundWord = sensitiveWordScanProvider.searchSensitiveWord("这串文本里 出售手枪 QQ 是敏感词, 但是用了 弱敏感级别，不能匹配这个normal级别的敏感词");
        Assert.assertEquals(foundWord.getFoundWord(), "出售手枪");
        Assert.assertEquals(foundWord.getStartIndex(), Integer.valueOf(6));
        Assert.assertEquals(foundWord.getEndIndex(), Integer.valueOf(9));
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.NANOSECONDS));
    }

    @Test
    public void searchAllSensitiveWord() {
        stopWatch.start("SearchAllSensitiveWord(String):List Timer");
        List<FoundWord> foundWords = sensitiveWordScanProvider.searchAllSensitiveWord("这串文本里 出售手枪 QQ 是敏感词, 但是用了 弱敏感级别，不能匹配这个normal级别的敏感词");
        Assert.assertEquals(foundWords.size(), 1);
        Assert.assertEquals(foundWords.get(0).getFoundWord(), "出售手枪");
        Assert.assertEquals(foundWords.get(0).getStartIndex(), Integer.valueOf(6));
        Assert.assertEquals(foundWords.get(0).getEndIndex(), Integer.valueOf(9));

        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.NANOSECONDS));
    }

    /**
     * 密集匹配 单测
     */
    @Test
    public void testSearchAllSensitiveWord() {
        stopWatch.start("SearchAllSensitiveWord(String, boolean):List Timer");
        List<FoundWord> foundWords = sensitiveWordScanProvider.searchAllSensitiveWord("这串文本里 出售手枪 QQ 是敏感词, 但是用了 弱敏感级别，不能匹配这个normal级别的敏感词", true);
        Assert.assertEquals(foundWords.size(), 3);
        Assert.assertEquals(foundWords.get(0).getFoundWord(), "出售手枪");
        Assert.assertEquals(foundWords.get(0).getStartIndex(), Integer.valueOf(6));
        Assert.assertEquals(foundWords.get(0).getEndIndex(), Integer.valueOf(9));

        Assert.assertEquals(foundWords.get(1).getFoundWord(), "手枪");
        Assert.assertEquals(foundWords.get(1).getStartIndex(), Integer.valueOf(8));
        Assert.assertEquals(foundWords.get(1).getEndIndex(), Integer.valueOf(9));

        Assert.assertEquals(foundWords.get(2).getFoundWord(), "出售手枪 QQ");
        Assert.assertEquals(foundWords.get(2).getStartIndex(), Integer.valueOf(6));
        Assert.assertEquals(foundWords.get(2).getEndIndex(), Integer.valueOf(12));


        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint(TimeUnit.NANOSECONDS));
    }

    @Test
    public void testFoundSensitiveWord() {
        String text = "太原市人民政府";
        List<FoundWord> foundWords = sensitiveWordScanProvider.searchAllSensitiveWord(text, true);
        System.out.println(sensitiveWordScanProvider.existsSensitiveWord(text));
        System.out.println(foundWords.stream().map(FoundWord::getFoundWord).collect(Collectors.joining(",")));
    }
}