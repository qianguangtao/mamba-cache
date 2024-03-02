package com.app.cache.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.app.cache.pojo.dto.CollectionCache;
import com.app.cache.service.CacheService;
import com.app.cache.utils.CacheNameSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author qiangt
 * @date 2023/9/15
 * @apiNote
 */
@RequiredArgsConstructor
@Slf4j
@Service("redisCacheService")
public class RedisCacheServiceImpl implements CacheService {

    private final StringRedisTemplate redisTemplate;

    private final CacheManager cacheManager;

    @Override
    public Object getCollectionCacheByKey(String prefix, String suffix, Method method) {
        final String key = this.getCollectionCacheKey(prefix, suffix);
        final String cacheValue = redisTemplate.opsForValue().get(key);
        if (ObjectUtil.isNotEmpty(cacheValue)) {
            Object o = null;
            try {
                o = JSON.parseObject(cacheValue, method.getGenericReturnType());
            } catch (Exception e) {
                // 缓存出错不抛异常，继续执行正常的业务查询
                log.error("Redis cache cast to {} exception", method.getGenericReturnType().getTypeName(), e);
            }
            return o;
        }
        return null;
    }

    private String getCollectionCacheKey(String prefix, String suffix) {
        return prefix + ":" + CACHE_MIDDLE_KEY + ":" + suffix;
    }

    @Override
    public void writeCollectionCache(CollectionCache collectionCache) {
        final Object result = collectionCache.getResult();
        final String prefix = collectionCache.getPrefix();
        final String suffix = collectionCache.getSuffix();
        if (ObjectUtil.isNotEmpty(result)) {
            final String cacheValue;
            try {
                cacheValue = JSON.toJSONString(result);
                String cacheKey = this.getCollectionCacheKey(prefix, suffix);
                redisTemplate.opsForValue().set(cacheKey, cacheValue, collectionCache.getCacheable().timeout(), collectionCache.getCacheable().unit());
            } catch (Exception e) {
                // 缓存出错不抛异常，继续执行正常的业务查询
                log.error("Redis cache string to json exception", e);
            }
        }
    }

    @Override
    public void removeList(String keyPrefix) {
        final String cacheKey = keyPrefix + ":" + CACHE_MIDDLE_KEY + ":*";
        final Set<String> keys = redisTemplate.keys(cacheKey);
        if (CollectionUtil.isNotEmpty(keys)) {
            keys.forEach(k -> {
                redisTemplate.delete(k);
            });
        }
    }

    @Override
    public void removeBatch(String cacheName, List idList) {
        Cache cache = cacheManager.getCache(cacheName);
        Assert.notNull(cache);
        idList.stream().forEach(id -> {
            cache.evict(id);
        });
    }

    @Override
    public void clear() {
        final Set<String> allCacheName = CacheNameSet.getAll();
        for (String cacheName : allCacheName) {
            final Set<String> keys = redisTemplate.keys(cacheName + ":*");
            if (CollectionUtil.isNotEmpty(keys)) {
                keys.forEach(k -> {
                    redisTemplate.delete(k);
                });
            }
        }
    }
}
