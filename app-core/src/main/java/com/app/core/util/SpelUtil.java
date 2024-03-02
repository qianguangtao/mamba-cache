package com.app.core.util;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.app.core.security.Principal;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/27 10:40
 * @description: spel工具类
 */
@Service
public class SpelUtil implements BeanFactoryAware {
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    private BeanFactory beanFactory;

    /**
     * @param spel   spring expression language表达式
     * @param params spel填充参数
     * @return spel计算后的值
     */
    public static Object calculate(String spel, Map<String, Object> params) {

        // 生成spel最终的结果
        return SpelUtil.calculate(spel, params, null);
    }

    /**
     * @param spel      spring expression language表达式
     * @param params    spel填充参数
     * @param principal 当前登录人
     * @return spel计算后的值
     */
    public static Object calculate(String spel, Map<String, Object> params, Principal principal) {
        ExpressionParser parser = new SpelExpressionParser();
        final Expression expression = parser.parseExpression(spel);
        EvaluationContext context = new StandardEvaluationContext();
        if (MapUtil.isNotEmpty(params)) {
            params.entrySet().stream().forEach(e -> {
                context.setVariable(e.getKey(), e.getValue());
            });
        }
        if (ObjectUtil.isNotNull(principal)) {
            context.setVariable("user", principal);
        }
        // 生成spel最终的结果
        return expression.getValue(context);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        // 填充evaluationContext对象的`BeanFactoryResolver`。
        this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
    }

    /**
     * 执行spring bean方法，比如"@testService.hello(#root)"
     * @param methodExpression
     * @param arguments
     * @return
     */
    public Object executeMethodExpression(String methodExpression, Object arguments) {
        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(methodExpression)
                .getValue(this.evaluationContext, arguments);
    }

    /**
     * 计算表达式的值，比如"#user.id"
     * @param spel   spring expression language
     * @param params 目标方法的所有参数
     * @return spel计算后的结果
     */
    public Object executeExpression(String spel, Map<String, Object> params) {
        ExpressionParser parser = new SpelExpressionParser();
        final Expression expression = parser.parseExpression(spel);
        EvaluationContext context = new StandardEvaluationContext();

        params.entrySet().stream().forEach(e -> {
            context.setVariable(e.getKey(), e.getValue());
        });
        // 生成spel最终的结果
        return expression.getValue(context);
    }

    /**
     * 计算表达式的值，比如"#user.id"
     * @param spel      spring expression language
     * @param keyPrefix spel key前缀，比如"#user.id"的"user"
     * @param param     参数
     * @return spel计算后的结果
     */
    public Object executeExpression(String spel, String keyPrefix, Object param) {
        ExpressionParser parser = new SpelExpressionParser();
        final Expression expression = parser.parseExpression(spel);
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable(keyPrefix, param);
        // 生成spel最终的结果
        return expression.getValue(context);
    }
}
