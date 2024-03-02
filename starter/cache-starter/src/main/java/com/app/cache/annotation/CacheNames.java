package com.app.cache.annotation;

import java.lang.annotation.*;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote 标识维护缓存name的枚举类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheNames {

}
