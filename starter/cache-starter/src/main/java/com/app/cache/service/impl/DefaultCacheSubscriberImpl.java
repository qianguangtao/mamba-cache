package com.app.cache.service.impl;

import com.app.cache.service.CacheSubscriber;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author qiangt
 * @date 2023/9/16
 * @apiNote 当cache使用redis时候，不用通知集群里的其他机器，这里设置一个没配syncType默认的Subscriber
 */
@ConditionalOnProperty(prefix = "app.cache", name = "sync-enabled", havingValue = "false")
@Service
public class DefaultCacheSubscriberImpl implements CacheSubscriber {

    /**
     * 默认订阅消息的实现为空即可，为了缓存类型Redis和Caffeine保持一致
     */
    @Override
    public void subscribeCacheTopic() {
        // Do nothing 作为不需要同步时候，CacheSubscriber的默认实现.
    }
}
