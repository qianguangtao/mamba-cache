package com.app.cache.service.impl;

import com.app.cache.pojo.dto.CacheDTO;
import com.app.cache.service.CachePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author qiangt
 * @date 2023/9/16
 * @apiNote
 */
@ConditionalOnProperty(prefix = "app.cache", name = "sync-enabled", havingValue = "true")
@Service
@Slf4j
@RequiredArgsConstructor
public class RedissonCachePublisherImpl implements CachePublisher {

    private final RedissonClient redissonClient;

    @Override
    public void publishCacheTopic(CacheDTO cacheDTO) {
        final RTopic topic = redissonClient.getTopic(cacheDTO.getCacheName());
        Long result = topic.publish(cacheDTO);
        log.debug("Redisson publisher result : {}" + result);
    }
}
