package com.app.redis.lock;

/**
 * @author qiangt
 * @date 2018.06.13
 */
public interface Lock {

    /**
     * 阻塞性的获取锁, 不响应中断
     *
     * @param key 锁ID
     */
    boolean lock(String key);

    /**
     * 阻塞性的获取锁, 不响应中断
     *
     * @param key          锁ID
     * @param spinDuration 自旋时长
     */
    boolean lock(String key, long spinDuration);

    /**
     * 阻塞性的获取锁, 响应中断
     *
     * @param key 锁ID
     */
    boolean lockInterruptibility(String key) throws InterruptedException;

    /**
     * 阻塞性的获取锁, 响应中断
     *
     * @param key          锁ID
     * @param spinDuration 自旋时长
     */
    boolean lockInterruptibility(String key, long spinDuration) throws InterruptedException;

    /**
     * 获取锁的默认持续时间
     */
    long getDefaultLockDuration();

    /**
     * 尝试获取锁, 获取不到立即返回, 不阻塞
     *
     * @param key 锁ID
     */
    boolean tryLock(String key);

    /**
     * 强制释放锁（不管锁有没有过期）
     *
     * @param key 锁ID
     */
    void unlock(String key);

    /**
     * 尝试释放锁（过期才释放，未过期不释放）
     */
    void tryUnlock(String key);

}
