package com.app.demo.cache.enums;

import com.app.cache.annotation.CacheNames;
import lombok.AllArgsConstructor;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote @CachePut(value = CacheEnum.Names.Student, key = "#student.id")
 * 中的value无法直接使用CacheEnum.Student.name()。
 * 注意保持CacheEnum的枚举name()和Names的值保持一致。
 */
@CacheNames
@AllArgsConstructor
public enum CacheEnum {
    /** 枚举name()用于spring cache的CacheManager管理所有缓存 */
    User(Names.User);

    /** code用于标记缓存的前缀，同枚举name()，只不过注解不能直接使用CacheEnum.Dict.name() */
    private final String code;

    public interface Names {
        String User = "User";
    }
}
