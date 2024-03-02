package com.app.cache.service.impl;

import com.app.cache.pojo.dto.CacheDTO;
import com.app.cache.service.CachePublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author qiangt
 * @date 2023/9/16
 * @apiNote 不用通知集群里的其他机器（单机Caffeine或集群cache使用redis），这里设置一个没配syncType默认的publisher
 */
@ConditionalOnProperty(prefix = "app.cache", name = "sync-enabled", havingValue = "false")
@Service
public class DefaultCachePublisherImpl implements CachePublisher {
    /**
     * 默认发布消息的实现为空即可，为了缓存类型Redis和Caffeine保持一致
     *
     * @param cacheDTO 缓存消息DTO
     */
    @Override
    public void publishCacheTopic(CacheDTO cacheDTO) {
        // Do nothing 作为不需要同步时候，CachePublisher的默认实现.
    }
}
