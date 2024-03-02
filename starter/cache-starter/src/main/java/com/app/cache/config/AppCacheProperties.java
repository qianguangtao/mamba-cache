package com.app.cache.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiangt
 * @date 2023/9/15
 * @apiNote
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.cache")
public class AppCacheProperties {
    /**
     * 是否开启caffeine缓存，false则只有redis一级缓存
     */
    private boolean caffeineEnabled;
    /**
     * 增删改是否需要同步集群中其他机器中的Caffeine
     */
    private boolean syncEnabled;
    /**
     * caffeine缓存过期时间，默认180s，可为空
     */
    private Long caffeineTimeout;
    /**
     * redis缓存过期时间，默认600s，可为空
     */
    private Long redisTimeout;
    /**
     * 添加了@CacheNames注解的枚举类package，可为空，空则扫描com.**
     */
    private String cacheNamesPackage;
}
