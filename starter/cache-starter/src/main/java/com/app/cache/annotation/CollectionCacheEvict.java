package com.app.cache.annotation;

import java.lang.annotation.*;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote 针对批量删除接口的清缓存注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CollectionCacheEvict {

    /**
     * 缓存前缀，即实体类名
     */
    String value() default "";

}
