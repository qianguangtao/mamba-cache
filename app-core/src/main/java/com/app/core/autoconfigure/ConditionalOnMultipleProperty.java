package com.app.core.autoconfigure;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @author qiangt
 * 使用示例：
 * @ConditionalOnMultipleProperty(prefix = "spring.app", name = "env", havingValue = {"dev", "sit"})
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnMultiplePropertyCondition.class)
public @interface ConditionalOnMultipleProperty {
    String prefix() default "";

    /**
     * 条件变量的name
     */
    String name() default "";

    /**
     * havingValue数组，支持or匹配
     */
    String[] havingValue() default {};
}
