package com.app.cache.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.app.cache.pojo.dto.CollectionCache;
import com.app.cache.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author qiangt
 * @date 2023/9/15
 * @apiNote 二级缓存操作类
 */
@RequiredArgsConstructor
@Slf4j
@Service("multiLevelCacheService")
public class MultiLevelCacheServiceImpl implements CacheService {

    @Qualifier("redisCacheService")
    @Resource
    private CacheService redisCacheService;

    @Qualifier("caffeineCacheService")
    @Resource
    private CacheService caffeineCacheService;

    @Override
    public Object getCollectionCacheByKey(String prefix, String suffix, Method method) {
        Object caffeineCache = caffeineCacheService.getCollectionCacheByKey(prefix, suffix, method);
        if (ObjectUtil.isNotNull(caffeineCache)) {
            return caffeineCache;
        } else {
            Object redisCache = redisCacheService.getCollectionCacheByKey(prefix, suffix, method);
            if (ObjectUtil.isNotNull(redisCache)) {
                caffeineCacheService.writeCollectionCache(CollectionCache.builder()
                        .prefix(prefix)
                        .suffix(suffix)
                        .result(redisCache)
                        .build());
            }
            return redisCache;
        }
    }

    @Override
    public void writeCollectionCache(CollectionCache collectionCache) {
        caffeineCacheService.writeCollectionCache(collectionCache);
        redisCacheService.writeCollectionCache(collectionCache);
    }

    @Override
    public void removeList(String keyPrefix) {
        caffeineCacheService.removeList(keyPrefix);
        redisCacheService.removeList(keyPrefix);
    }

    @Override
    public void removeBatch(String cacheName, List idList) {
        caffeineCacheService.removeBatch(cacheName, idList);
        redisCacheService.removeBatch(cacheName, idList);
    }

    @Override
    public void clear() {
        caffeineCacheService.clear();
        redisCacheService.clear();
    }
}
