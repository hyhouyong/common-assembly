package com.kcwl.common.risk.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ckwl
 */
public class SimplePasswordUtil {
    /**
     * 校验包含连续大于等于5位
     */
    private static String CONTINUOUS_REPETITION = ".*([0-9a-zA-Z])\\1{4,9}.*";
    private static Pattern CONTINUOUS_REPETITION_PATTERN = Pattern.compile(CONTINUOUS_REPETITION);

    /**
     * 检索 5 到 10 位连续递增数字，01234可以匹配 ，67890 不可以匹配
     */
    private static String CONTINUOUS_INCREMENT = ".*((?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){4,9}\\d).*";
    private static Pattern CONTINUOUS_INCREMENT_PATTERN = Pattern.compile(CONTINUOUS_INCREMENT);

    /**
     * 检索 5 到 10 位连续递减数字，43210可以匹配，09876 不可以匹配
     */
    private static String CONTINUOUS_DECREMENT = ".*((?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){4,9}\\d).*";
    private static Pattern CONTINUOUS_DECREMENT_PATTERN = Pattern.compile(CONTINUOUS_DECREMENT);

    /**
     * 检索 5 到 10 位连续递增小写字母
     */
    private static String LOWERCASE_INCREMENT = ".*((?:a(?=b)|b(?=c)|c(?=d)|d(?=e)|e(?=f)|f(?=g)|g(?=h)|h(?=i)|i(?=j)|j(?=k)|k(?=l)|l(?=m)|m(?=n)|n(?=o)|o(?=p)|p(?=q)|q(?=r)|r(?=s)|s(?=t)|t(?=u)|u(?=v)|v(?=w)|w(?=x)|x(?=y)|y(?=z)){4,9}\\w).*";
    private static Pattern LOWERCASE_INCREMENT_PATTERN = Pattern.compile(LOWERCASE_INCREMENT);

    /**
     * 检索 5 到 10 位连续递增大写字母
     */
    private static String UPPERCASE_INCREMENT = ".*((?:A(?=B)|B(?=C)|C(?=D)|D(?=E)|E(?=F)|F(?=G)|G(?=H)|H(?=I)|I(?=J)|J(?=K)|K(?=L)|L(?=M)|M(?=N)|N(?=O)|O(?=P)|P(?=Q)|Q(?=R)|R(?=S)|S(?=T)|T(?=U)|U(?=V)|V(?=W)|W(?=X)|X(?=Y)|Y(?=Z)){4,9}\\w).*";
    private static Pattern UPPERCASE_INCREMENT_PATTERN = Pattern.compile(UPPERCASE_INCREMENT);

    /**
     *
     * 检索 5 到 10 位连续递减小写字母
     */
    private static String LOWERCASE_DECREMENT = ".*((?:z(?=y)|y(?=x)|x(?=w)|w(?=v)|v(?=u)|u(?=t)|t(?=s)|s(?=r)|r(?=q)|q(?=p)|p(?=o)|o(?=n)|n(?=m)|m(?=l)|l(?=k)|k(?=j)|j(?=i)|i(?=h)|h(?=g)|g(?=f)|f(?=e)|e(?=d)|d(?=c)|c(?=b)|b(?=a)){4,9}\\w).*";
    private static Pattern LOWERCASE_DECREMENT_PATTERN = Pattern.compile(LOWERCASE_DECREMENT);

    /**
     *
     * 检索 5 到 10 位连续递减大写字母
     */
    private static String UPPERCASE_DECREMENT = ".*((?:Z(?=Y)|Y(?=X)|X(?=W)|W(?=V)|V(?=U)|U(?=T)|T(?=S)|S(?=R)|R(?=Q)|Q(?=P)|P(?=O)|O(?=N)|N(?=M)|M(?=L)|L(?=K)|K(?=J)|J(?=I)|I(?=H)|H(?=G)|G(?=F)|F(?=E)|E(?=D)|D(?=C)|C(?=B)|B(?=A)){4,9}\\w).*";
    private static Pattern UPPERCASE_DECREMENT_PATTERN = Pattern.compile(UPPERCASE_DECREMENT);

    public static boolean checkRepeat(String str) {
        Matcher matcher = CONTINUOUS_REPETITION_PATTERN.matcher(str);
        return matcher.matches();
    }

    public static boolean checkDigitIncrement(String str) {
        Matcher matcher = CONTINUOUS_INCREMENT_PATTERN.matcher(str);
        return matcher.matches();
    }

    public static boolean checkDigitDecrement(String str) {
        Matcher matcher = CONTINUOUS_DECREMENT_PATTERN.matcher(str);
        return matcher.matches();
    }

    public static boolean checkLowercaseIncrement(String str) {
        Matcher matcher = LOWERCASE_INCREMENT_PATTERN.matcher(str);
        return matcher.matches();
    }

    public static boolean checkUppercaseIncrement(String str) {
        Matcher matcher = UPPERCASE_INCREMENT_PATTERN.matcher(str);
        return matcher.matches();
    }

    public static boolean checkLowercaseDecrement(String str) {
        Matcher matcher = LOWERCASE_DECREMENT_PATTERN.matcher(str);
        return matcher.matches();
    }

    public static boolean checkUppercaseDecrement(String str) {
        Matcher matcher = UPPERCASE_DECREMENT_PATTERN.matcher(str);
        return matcher.matches();
    }

}
