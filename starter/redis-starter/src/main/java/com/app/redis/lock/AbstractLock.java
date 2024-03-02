package com.app.redis.lock;

import lombok.extern.slf4j.Slf4j;

/**
 * 这里需不需要保证可见性得讨论，因为是分布式的锁。</br>
 * 1 -> 同一个jvm的多个线程使用不同的锁对象其实也是可以的，这种情况下不需要保证可见性。</br>
 * 2 -> 同一个jvm的多个线程使用同一个锁对象，那可见性就必须要保证了。</br>
 *
 * @author qiangt
 * @date 2018.06.13
 */
@Slf4j
public abstract class AbstractLock implements Lock {

    @Override
    public boolean lock(final String key) {
        try {
            return this.doLock(key, false, this.getDefaultLockDuration(), false);
        } catch (final InterruptedException e) {
            log.info("当前线程:" + Thread.currentThread().getName() + "已经被中中止");
            return false;
        }
    }

    @Override
    public boolean lock(final String key, final long duration) {
        try {
            return this.doLock(key, true, duration, false);
        } catch (final InterruptedException e) {
            log.info("当前线程:" + Thread.currentThread().getName() + "已经被中止");
            return false;
        }
    }

    @Override
    public boolean lockInterruptibility(final String key) throws InterruptedException {
        return this.doLock(key, false, this.getDefaultLockDuration(), true);
    }

    @Override
    public boolean lockInterruptibility(final String key, final long duration) throws InterruptedException {
        return this.doLock(key, true, duration, true);
    }

    @Override
    public boolean tryLock(final String key) {
        try {
            return this.doTryLock(key);
        } catch (final InterruptedException e) {
            log.info("当前线程:" + Thread.currentThread().getName() + "已经被中止");
        }
        return false;
    }

    @Override
    public void unlock(final String key) {
        this.doUnlock(key);
    }

    @Override
    public void tryUnlock(final String key) {
        this.doTryUnlock(key);
    }

    /**
     * 子类实现释放锁的功能（不管有没有过期，强制释放）
     */
    protected abstract void doTryUnlock(String key);

    /**
     * 子类实现释放锁的功能
     */
    protected abstract void doUnlock(String key);

    /**
     * 非阻塞式获取锁的实现
     *
     * @param key 锁的id
     * @return boolean 获取锁是否成功
     */
    protected abstract boolean doTryLock(String key) throws InterruptedException;

    /**
     * 阻塞式获取锁的实现
     *
     * @param key            锁的id
     * @param checkTimeout   该线程自旋时是否启用超时等待
     * @param duration       时间单位数量
     * @param checkInterrupt 是否响应中断
     * @return boolean 获取锁是否成功
     */
    protected abstract boolean doLock(String key, boolean checkTimeout, long duration, boolean checkInterrupt) throws InterruptedException;

}
