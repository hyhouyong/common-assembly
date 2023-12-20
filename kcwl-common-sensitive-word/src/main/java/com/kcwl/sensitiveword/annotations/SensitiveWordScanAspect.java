package com.kcwl.sensitiveword.annotations;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.kcwl.sensitiveword.exception.SensitiveWordParseException;
import com.kcwl.sensitiveword.exception.SensitiveWordScanException;
import com.kcwl.sensitiveword.provider.SensitiveWordScanProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.*;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * <p>
 * 敏感词检测 声明式 aop
 * </p>
 *
 * @author renyp
 * @since 2023/6/1 17:11
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class SensitiveWordScanAspect {

    @Pointcut("@annotation(com.kcwl.sensitiveword.annotations.SensitiveWordScanner)")
    public void declareSensitiveWordScanPointcut() {
    }

    private final SensitiveWordScanProvider sensitiveWordScanProvider;

    private final ExpressionParser expressionParser;

    @Around("declareSensitiveWordScanPointcut()")
    public Object sensitiveWordScanBeforeMethodInvoke(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            SensitiveWordScanner annotation = method.getAnnotation(SensitiveWordScanner.class);
            String[] textGenerators = annotation.textGenerator();
            if (ArrayUtil.isNotEmpty(textGenerators)) {
                // 将方法的入参放入SPEL的context
                String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
                Object[] args = joinPoint.getArgs();
                if (parameterNames.length != args.length) {
                    log.error("敏感词检测，方法入参个 方法入参名 个数不同, 方法入参: {}, 方法入参名: {}", args, parameterNames);
                    throw new SensitiveWordParseException();
                }
                EvaluationContext evaluationContext = new StandardEvaluationContext();
                for (int i = 0; i < parameterNames.length; i++) {
                    evaluationContext.setVariable(parameterNames[i], args[i]);
                }
                // 2023/6/1 解析表达式，并执行
                for (String textGenerator : textGenerators) {
                    try {
                        Expression expression = expressionParser.parseExpression(textGenerator);
                        String userInputText = expression.getValue(evaluationContext, String.class);
                        if (StrUtil.isNotBlank(userInputText) && sensitiveWordScanProvider.existsSensitiveWord(userInputText, annotation.level())) {
                            log.error("敏感词检测，用户输入包含敏感词，用户输入：{}", userInputText);
                            throw new SensitiveWordScanException();
                        }
                    } catch (ParseException parseException) {
                        log.error("敏感词检测，获取待检测文本的表达式解析异常：{}", textGenerator, parseException);
                        throw new SensitiveWordParseException();
                    } catch (EvaluationException evaluationException) {
                        log.error("敏感词检测，获取待检测文本的表达式回调异常，表达式：{}，上下文：{}", textGenerator, JSONUtil.toJsonStr(evaluationContext), evaluationException);
                        throw new SensitiveWordParseException();
                    }
                }
            }
            if (StrUtil.isNotBlank(annotation.value()) && sensitiveWordScanProvider.existsSensitiveWord(annotation.value(), annotation.level())) {
                log.error("敏感词检测，用户输入包含敏感词，用户输入：{}", annotation.value());
                throw new SensitiveWordScanException();
            }
        } catch (SensitiveWordScanException ex) {
            throw ex;
        } catch (SensitiveWordParseException ex) {
            // do nothing
        } catch (Exception ex) {
            log.error("敏感词检测异常：", ex);
        }
        return joinPoint.proceed();
    }
}
