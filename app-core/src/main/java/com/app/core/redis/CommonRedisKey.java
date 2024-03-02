package com.app.core.redis;

import java.util.concurrent.TimeUnit;

/**
 * CommonRedisKey
 *
 * @author qiangt
 * @since 2021-09-06
 */
public enum CommonRedisKey implements RedisCacheKey {

    /** 多线程事务 */
    ConcurrentTransactionKey("多线程事务", 3, TimeUnit.MINUTES),
    /** 当前登录人 */
    UserInfo("当前登录人", 1, TimeUnit.DAYS),
    /** redisson分布式锁 */
    RedissonLockKey("redisson分布式锁", 12000, TimeUnit.MILLISECONDS);

    private final String comment;
    private final long timeout;
    private final TimeUnit unit;

    CommonRedisKey(final String comment, final long timeout, final TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        this.comment = comment;
    }

    @Override
    public String comment() {
        return this.comment;
    }

    public long timeout() {
        return this.timeout;
    }

    public TimeUnit unit() {
        return this.unit;
    }

}
