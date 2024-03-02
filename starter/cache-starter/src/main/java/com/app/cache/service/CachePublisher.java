package com.app.cache.service;

import com.app.cache.pojo.dto.CacheDTO;

/**
 * 缓存发布接口
 *
 * @author 10071
 */
public interface CachePublisher {
    /**
     * 发布缓存同步消息
     *
     * @param cacheDTO 缓存消息DTO
     */
    void publishCacheTopic(CacheDTO cacheDTO);
}
