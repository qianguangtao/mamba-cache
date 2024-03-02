package com.app.cache.config;

import cn.hutool.core.util.ObjectUtil;
import com.app.cache.utils.CacheNameSet;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@ComponentScan("com.app.cache")
public class CacheConfig {

    private final static Long DEFAULT_REDIS_TIMEOUT = 600L;
    private final static Long DEFAULT_CAFFEINE_TIMEOUT = 180L;
    private final AppCacheProperties appCacheProperties;

    @Primary
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory factory) {
        Long timeout = ObjectUtil.isNotNull(appCacheProperties.getRedisTimeout()) ? appCacheProperties.getRedisTimeout() : DEFAULT_REDIS_TIMEOUT;
        // Duration.ofSeconds(180)设置缓存默认过期时间180秒
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(timeout));
        // 解决使用@Cacheable，redis数据库value乱码
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
        return RedisCacheManager.builder(factory).cacheDefaults(config).build();
    }

    @Bean("caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        ArrayList<CaffeineCache> caches = new ArrayList<>();
        // String cacheName(): 创建缓存名称
        // Cache<Object, Object> generateCache(): 创建一个Caffeine缓存
        final Set<String> nameSet = CacheNameSet.getAll();
        log.info("Find Caffeine cache {}", nameSet);
        nameSet.forEach(name -> {
            log.info("Caffeine cache {} init", name);
            caches.add(new CaffeineCache(name, caffeineCache()));
        });
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private Cache<Object, Object> caffeineCache() {
        Long timeout = ObjectUtil.isNotNull(appCacheProperties.getCaffeineTimeout()) ? appCacheProperties.getCaffeineTimeout() : DEFAULT_CAFFEINE_TIMEOUT;
        return Caffeine.newBuilder()
                .expireAfterWrite(timeout, TimeUnit.SECONDS)
                .maximumSize(1000)
                .initialCapacity(100)
                .build();
    }
}
