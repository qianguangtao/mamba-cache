package com.app.cache.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.app.cache.config.AppCacheProperties;
import com.app.cache.pojo.dto.CollectionCache;
import com.app.cache.service.CacheService;
import com.app.cache.utils.CacheNameSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author qiangt
 * @date 2023/9/15
 * @apiNote
 */
@RequiredArgsConstructor
@Slf4j
@Service("caffeineCacheService")
public class CaffeineCacheServiceImpl implements CacheService {

    private final AppCacheProperties cacheProperties;
    @Resource
    @Qualifier("caffeineCacheManager")
    private CacheManager cacheManager;

    @Override
    public Object getCollectionCacheByKey(String prefix, String suffix, Method method) {
        if (!cacheProperties.isCaffeineEnabled()) {
            return null;
        }
        final Cache cache = cacheManager.getCache(prefix);
        Assert.notNull(cache);
        return cache.get(CACHE_MIDDLE_KEY + ":" + suffix, method.getReturnType());
    }

    @Override
    public void writeCollectionCache(CollectionCache collectionCache) {
        if (!cacheProperties.isCaffeineEnabled()) {
            return;
        }
        final Object result = collectionCache.getResult();
        final String prefix = collectionCache.getPrefix();
        final String suffix = collectionCache.getSuffix();
        final Cache cache = cacheManager.getCache(prefix);
        Assert.notNull(cache);
        cache.put(CACHE_MIDDLE_KEY + ":" + suffix, result);
    }

    @Override
    public void removeList(String keyPrefix) {
        if (!cacheProperties.isCaffeineEnabled()) {
            return;
        }
        final List<Object> keyList = this.cachekeyList(keyPrefix, CACHE_MIDDLE_KEY + ":");
        final Cache cache = cacheManager.getCache(keyPrefix);
        keyList.stream().forEach(k -> {
            cache.evict(k);
        });
    }

    @Override
    public void removeBatch(String cacheName, List idList) {
        if (!cacheProperties.isCaffeineEnabled()) {
            return;
        }
        Cache cache = cacheManager.getCache(cacheName);
        Assert.notNull(cache);
        idList.stream().forEach(id -> {
            cache.evict(id);
        });
    }

    @Override
    public void clear() {
        if (!cacheProperties.isCaffeineEnabled()) {
            return;
        }
        final Set<String> allCacheName = CacheNameSet.getAll();
        for (String cacheName : allCacheName) {
            final Cache cache = cacheManager.getCache(cacheName);
            if (ObjectUtil.isNotNull(cache)) {
                final Map<Object, Object> allCache = this.getAllCache(cache);
                allCache.entrySet().stream().forEach(e -> {
                    cache.evict(e.getKey());
                });
            }
        }
    }

    /**
     * @author qiangt
     * @date 2023/9/15
     * @apiNote 使用正则模糊查询缓存中所有以likeKey开头的key（ a like 'b%'）
     */
    private List<Object> cachekeyList(String prefix, String likeKey) {
        if (ObjectUtil.isNotEmpty(prefix) && ObjectUtil.isNotEmpty(likeKey)) {
            final Cache cache = cacheManager.getCache(prefix);
            final Pattern pattern = Pattern.compile("^" + Pattern.quote(likeKey));
            final Map<Object, Object> allCache = this.getAllCache(cache);
            if (ObjectUtil.isNotEmpty(allCache)) {
                final List<Object> keyList = allCache.entrySet().stream().map(e -> {
                    return e.getKey();
                }).filter(k -> {
                    final Matcher matcher = pattern.matcher(Convert.toStr(k));
                    return matcher.find();
                }).collect(Collectors.toList());
                return ObjectUtil.isNotEmpty(keyList) ? keyList : Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }
}
