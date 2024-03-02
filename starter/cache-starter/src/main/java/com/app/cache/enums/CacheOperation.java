package com.app.cache.enums;

/**
 * @author qiangt
 * @date 2023/9/14
 * @apiNote 缓存操作类型
 */
public enum CacheOperation {
    /**
     * 缓存操作类型：新增or编辑
     */
    PUT,
    /**
     * 缓存操作类型：删除单条
     */
    Evict,
    /**
     * 缓存操作类型：批量删除
     */
    CollectionEvict
}
