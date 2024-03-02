package com.app.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CollectionCacheable {

    /**
     * 缓存前缀，即实体类名
     */
    String value() default "";

    /**
     * 设置默认缓存时间
     */
    long timeout() default 180;

    /**
     * 设置默认缓存时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

}
