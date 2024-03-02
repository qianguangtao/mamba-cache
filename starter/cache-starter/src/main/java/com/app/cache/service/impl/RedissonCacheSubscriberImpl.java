package com.app.cache.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.app.cache.config.AppCacheProperties;
import com.app.cache.pojo.dto.CacheDTO;
import com.app.cache.service.CacheService;
import com.app.cache.service.CacheSubscriber;
import com.app.cache.utils.CacheNameSet;
import com.app.cache.utils.InstanceIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiangt
 * @date 2023/9/16
 * @apiNote
 */
@ConditionalOnProperty(prefix = "app.cache", name = "sync-enabled", havingValue = "true")
@Service
@Slf4j
@RequiredArgsConstructor
public class RedissonCacheSubscriberImpl implements CacheSubscriber {

    private final RedissonClient redissonClient;
    private final AppCacheProperties appCacheProperties;
    @Qualifier("multiLevelCacheService")
    @Resource
    private CacheService cacheService;

    @Override
    public void subscribeCacheTopic() {
        // 使用caffeine作为缓存的时候才订阅redisson的topic
        if (!appCacheProperties.isSyncEnabled()) {
            return;
        }
        CacheNameSet.getAll().forEach(name -> {
            final RTopic topic = redissonClient.getTopic(name);
            topic.addListener(CacheDTO.class, new MessageListener<CacheDTO>() {
                @Override
                public void onMessage(CharSequence charSequence, CacheDTO cacheDTO) {
                    log.info("Caffeine cache instance [{}] received message : {}", InstanceIdGenerator.INSTANCE.getInstanceId(), cacheDTO.toString());
                    // 如果subscriber接收的instanceId和yml中一样，说明是自己发给自己，不处理
                    if (ObjectUtil.equals(cacheDTO.getInstanceId(), InstanceIdGenerator.INSTANCE.getInstanceId())) {
                        return;
                    }
                    cacheService.removeList(cacheDTO.getCacheName());
                }
            });
        });
    }
}
