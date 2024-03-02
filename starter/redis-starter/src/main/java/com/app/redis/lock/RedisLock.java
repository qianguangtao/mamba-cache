package com.app.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author qiangt
 * @since 2018.06.13
 */
@Slf4j
public class RedisLock extends AbstractLock {

    private static final long DEFAULT_EXPIRE_DURATION = 300000L;
    private final RedisTemplate<String, String> redisTemplate;

    public RedisLock(final StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doTryUnlock(final String key) {
        final String value = this.redisTemplate.boundValueOps(key).get();
        // 判断锁是否过期
        if (value != null && this.timeIsExpired(Long.valueOf(value))) {
            this.redisTemplate.delete(key);
        }
    }

    @Override
    protected void doUnlock(final String key) {
        this.redisTemplate.delete(key);
    }

    @Override
    public boolean doTryLock(final String key) {
        // redis中设值成功，此时该线程获得锁
        if (Boolean.TRUE.equals(this.redisTemplate.boundValueOps(key).setIfAbsent(String.valueOf(System.currentTimeMillis())))) {
            this.redisTemplate.expire(key, this.getDefaultLockDuration(), TimeUnit.MILLISECONDS);
            // 成功获取到锁, 设置相关标识
            log.info("当前线程:" + Thread.currentThread().getName() + "成功获取到锁：" + key);
            return true;
        }
        return false;
    }

    @Override
    protected boolean doLock(final String key, final boolean checkTimeOut, final long spinDuration, final boolean checkInterrupt) throws InterruptedException {
        final long start = System.currentTimeMillis();
        if (checkInterrupt) {
            this.checkInterruption();
        }
        // 锁超时时间
        final String expireTimeMillis = String.valueOf(System.currentTimeMillis() + spinDuration);
        while (!checkTimeOut || !this.timeIsOut(start, spinDuration)) {
            if (checkInterrupt) {
                this.checkInterruption();
            }
            // 在redis中设值成功，获取到锁
            if (Boolean.TRUE.equals(this.redisTemplate.boundValueOps(key).setIfAbsent(expireTimeMillis))) {
                this.redisTemplate.expire(key, this.getDefaultLockDuration(), TimeUnit.MILLISECONDS);
                // 成功获取到锁, 设置相关标识
                log.info("当前线程:" + Thread.currentThread().getName() + "成功获取到锁：" + key);
                return true;
            }
            // 在redis中设值失败
            final String value = this.redisTemplate.boundValueOps(key).get();
            // 锁已经过期
            if (value != null && this.timeIsExpired(Long.valueOf(value))) {
                // 假设多个线程(多个jvm)同时走到这里
                // getSet方法是原子性的
                final String oldValue = this.redisTemplate.boundValueOps(key).getAndSet(expireTimeMillis);
                // 但是走到这里时每个线程拿到的oldValue肯定不可能一样(因为getset是原子性的)
                // 假如拿到的oldValue依然是expired的，那么就此时没有其他线程获得锁，则该线程获得锁
                // 第一个拿到的值肯定是超时的，后面陆续拿到的值肯定不是超时的，所以第一个拿到的值获得锁
                if (oldValue != null && this.timeIsExpired(Long.valueOf(oldValue))) {
                    // 成功获取到锁, 设置相关标识
                    log.info("当前线程:" + Thread.currentThread().getName() + "成功获取到锁：" + key);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测当前线程是否被中断
     */
    private void checkInterruption() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            log.info("当前线程：" + Thread.currentThread().getName() + "被中断");
            throw new InterruptedException();
        }
    }

    /**
     * 检测是否超时
     */
    private boolean timeIsOut(final long start, final long duration) {
        return start + duration < System.currentTimeMillis();
    }

    @Override
    public long getDefaultLockDuration() {
        return DEFAULT_EXPIRE_DURATION;
    }

    /**
     * 检测锁是否过期
     */
    private boolean timeIsExpired(final Long value) {
        if (value == null) {
            return true;
        }
        return value < System.currentTimeMillis();
    }

}
