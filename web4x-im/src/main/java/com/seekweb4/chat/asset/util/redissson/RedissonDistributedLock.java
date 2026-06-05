package com.seekweb4.chat.asset.util.redissson;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author coderpwh
 */
@Component
public class RedissonDistributedLock {

    private final RedissonClient redissonClient;

    public RedissonDistributedLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 阻塞式获取锁（如果 leaseTime > 0 则为固定租期，leaseTime <= 0 则使用 watchdog 自动续租）
     * @param lockKey key
     * @param leaseTime 锁租期，单位 unit。leaseTime <= 0 时启用 Redisson watchdog（自动续租），直到 unlock 或 JVM 退出。
     * @param unit 时间单位
     */
    public RLock lock(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        if (leaseTime > 0) {
            // 固定租期，lease 到期后自动解锁
            lock.lock(leaseTime, unit);
        } else {
            // 使用 watchdog 自动续租（默认 30s），直到显式 unlock
            lock.lock();
        }
        return lock;
    }

    /**
     * 非阻塞尝试获取锁（立即返回）
     * @return RLock（已持有）或 null（获取失败）
     */
    public RLock tryLockNow(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean ok = lock.tryLock(0, leaseTime, unit);
            return ok ? lock : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * 带等待时间的 tryLock（最多等待 waitTime 去尝试拿锁）
     */
    public RLock tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean ok = lock.tryLock(waitTime, leaseTime, unit);
            return ok ? lock : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * 释放锁（只需调用 lock.unlock()）
     */
    public void unlock(RLock lock) {
        if (lock == null) return;
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            } else {
                // 当前线程未持有锁：可记录日志或忽略
            }
        } catch (IllegalMonitorStateException ignore) {
            // 可能锁已过期/已被释放，忽略或记录
        }
    }


}
