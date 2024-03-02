package com.app.cache.service;

import cn.hutool.core.util.ObjectUtil;
import com.app.cache.pojo.dto.CollectionCache;
import org.springframework.cache.Cache;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiangt
 * @date 2023/9/15
 * @apiNote
 */
public interface CacheService {

    /**
     * 集合缓存中间的key
     */
    String CACHE_MIDDLE_KEY = "List";

    /**
     * 根据key获取缓存中的对象
     *
     * @param prefix 缓存前缀
     * @param suffix 缓存后缀
     * @param method 从Method反射中获取转换类型
     * @return 根据key获取缓存中的对象
     */
    Object getCollectionCacheByKey(String prefix, String suffix, Method method);

    /**
     * 向缓存中写入集合数据
     *
     * @param collectionCache
     */
    void writeCollectionCache(CollectionCache collectionCache);

    /**
     * 根据key前缀，批量删除缓存中的list
     *
     * @param keyPrefix
     */
    void removeList(String keyPrefix);

    /**
     * 根据cacheName，删除指定的idList
     *
     * @param cacheName
     * @param idList
     */
    void removeBatch(String cacheName, List idList);

    /**
     * 清空所有的缓存
     */
    void clear();

    /**
     * 从CacheManager中获取所有缓存
     *
     * @param cache 从spring CacheManger中获取的Cache
     * @return Cache中的所有数据转map
     */
    default Map<Object, Object> getAllCache(Cache cache) {
        Object obj = cache.getNativeCache();
        Map<String, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                ReflectionUtils.makeAccessible(field);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            return Collections.emptyMap();
        }
        // 获取Cache.map中的cache
        return ObjectUtil.isNotEmpty(map.get("cache")) ? (Map<Object, Object>) map.get("cache") : Collections.emptyMap();
    }

}
