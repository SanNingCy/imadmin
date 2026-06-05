package com.seekweb4.chat.delayedQueue;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 延时队列
 */
@Component
@Slf4j
public class RedisDelayedQueue {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 添加对象进消息队列
     * @param putInData 添加数据
     * @param queueName 队列名称
     * @param <T>
     */
    private <T> void addQueue(T putInData, String queueName){
        log.info("添加消息队列,监听名称:{},内容:{}", queueName, putInData);
        RBlockingQueue<T> blockingQueue = redissonClient.getBlockingQueue(queueName);
        blockingQueue.offer(putInData);
    }
    /**
     * 添加对象进消息队列
     * @param putInData 添加数据
     * @param clazz 队列执行类
     * @param <T>
     */
    public <T> void addQueue(T putInData, Class<? extends RedisDelayedQueueListener> clazz){
        addQueue(putInData, clazz.getSimpleName());
    }

    /**
     * 添加对象进延时队列
     * @param putInData 添加数据
     * @param delay     延时时间
     * @param timeUnit  时间单位
     * @param queueName 队列名称
     * @param <T>
     */
    private <T> void addQueue(T putInData,long delay, TimeUnit timeUnit, String queueName){
        log.info("添加延迟队列,监听名称:{},时间:{},时间单位:{},内容:{}", queueName, delay, timeUnit,putInData);
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(putInData, delay, timeUnit);
    }

    /**
     * 添加队列-秒
     *
     * @param t     DTO传输类
     * @param delay 时间数量
     * @param <T>   泛型
     */
    public <T> void addQueueSeconds(T t, long delay, Class<? extends RedisDelayedQueueListener> clazz) {
        addQueue(t, delay, TimeUnit.SECONDS, clazz.getSimpleName());
    }

    /**
     * 添加队列-分
     *
     * @param t     DTO传输类
     * @param delay 时间数量
     * @param <T>   泛型
     */
    public <T> void addQueueMinutes(T t, long delay, Class<? extends RedisDelayedQueueListener> clazz) {
        addQueue(t, delay, TimeUnit.MINUTES, clazz.getSimpleName());
    }

    /**
     * 添加队列-时
     *
     * @param t     DTO传输类
     * @param delay 时间数量
     * @param <T>   泛型
     */
    public <T> void addQueueHours(T t, long delay, Class<? extends RedisDelayedQueueListener> clazz) {
        addQueue(t, delay, TimeUnit.HOURS, clazz.getSimpleName());
    }
    /**
     * 添加队列-天
     *
     * @param t     DTO传输类
     * @param delay 时间数量
     * @param <T>   泛型
     */
    public <T> void addQueueDays(T t, long delay, Class<? extends RedisDelayedQueueListener> clazz) {
        addQueue(t, delay, TimeUnit.DAYS, clazz.getSimpleName());
    }

    /**
     * 删除延时队列
     * @param t 数据
     * @param clazz 队列名称
     * @param <T>
     */
    public <T> void removeDelayedQueue(T t, Class<? extends RedisDelayedQueueListener> clazz) {
        RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(clazz.getSimpleName());
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.remove(t);
    }
}
