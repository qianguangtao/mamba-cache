package com.app.redis.lock;

import com.app.kit.SpringKit;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author qiangt
 * @date 2018.06.14
 */
public final class LockUtil {

    private static final StringRedisTemplate redisTemplate = SpringKit.getBean(StringRedisTemplate.class);

    /**
     * 阻塞性的获取锁，不响应中断
     */
    public static boolean lock(final String key) {
        return new RedisLock(redisTemplate).lock(key);
    }

    /**
     * 阻塞性的获取锁, 不响应中断
     *
     * @param spinDuration 自旋时长
     */
    public static boolean lock(final String key, final long spinDuration) {
        return new RedisLock(redisTemplate).lock(key, spinDuration);
    }

    /**
     * 阻塞性的获取锁, 响应中断
     */
    public static boolean lockInterruptibility(final String key) throws InterruptedException {
        return new RedisLock(redisTemplate).lockInterruptibility(key);
    }

    /**
     * 阻塞性的获取锁, 响应中断
     *
     * @param spinDuration 自旋时长
     */
    public static boolean lockInterruptibility(final String key, final long spinDuration) throws InterruptedException {
        return new RedisLock(redisTemplate).lockInterruptibility(key, spinDuration);
    }

    /**
     * 尝试获取锁, 获取不到立即返回, 不阻塞
     */
    public static boolean tryLock(final String key) {
        return new RedisLock(redisTemplate).tryLock(key);
    }

    /**
     * 释放锁
     */
    public static void unlock(final String key) {
        new RedisLock(redisTemplate).unlock(key);
    }

    /**
     * 释放锁（不管有没有过期，强制释放）
     */
    public static void tryUnlock(final String key) {
        new RedisLock(redisTemplate).tryUnlock(key);
    }

}
