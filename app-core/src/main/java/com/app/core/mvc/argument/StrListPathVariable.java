package com.app.core.mvc.argument;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/11/6 11:56
 * @description: 前端url参数"a,b,c"使用List接收
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrListPathVariable {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    boolean required() default true;
}
