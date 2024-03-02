package com.app.cache.service;

/**
 * @author qiangt
 * @date 2023/9/16
 * @apiNote
 */
public interface CacheSubscriber {
    /**
     * 订阅缓存消息
     */
    void subscribeCacheTopic();
}
