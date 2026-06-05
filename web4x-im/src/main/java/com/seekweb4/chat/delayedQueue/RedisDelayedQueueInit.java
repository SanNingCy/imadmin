package com.seekweb4.chat.delayedQueue;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * redis 延时队列初始化
 */
@Component
@Slf4j
public class RedisDelayedQueueInit implements CommandLineRunner {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ApplicationContext applicationContext;
    
    private ThreadPoolTaskExecutor delayedQueueExecutor;
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    
    /**
     * 获取应用上下文并获取相应的接口实现类
     */
    @Override
    public void run(String... args) throws Exception {
        // 初始化线程池
        initThreadPool();
        
        // 使用队列时需要放开注释
        Map<String, RedisDelayedQueueListener> map = applicationContext.getBeansOfType(RedisDelayedQueueListener.class);
        log.info("发现{}个延迟队列监听器", map.size());
        
        for (Map.Entry<String, RedisDelayedQueueListener> taskEventListenerEntry : map.entrySet()) {
            String listenerName = taskEventListenerEntry.getValue().getClass().getSimpleName();
            startThread(listenerName, taskEventListenerEntry.getValue());
        }
    }
    
    /**
     * 初始化线程池
     */
    private void initThreadPool() {
        delayedQueueExecutor = new ThreadPoolTaskExecutor();
        delayedQueueExecutor.setCorePoolSize(5);
        delayedQueueExecutor.setMaxPoolSize(10);
        delayedQueueExecutor.setQueueCapacity(50);
        delayedQueueExecutor.setThreadNamePrefix("DelayedQueue-");
        delayedQueueExecutor.setKeepAliveSeconds(60);
        delayedQueueExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        delayedQueueExecutor.setWaitForTasksToCompleteOnShutdown(true);
        delayedQueueExecutor.setAwaitTerminationSeconds(30);
        delayedQueueExecutor.initialize();
        
        log.info("延迟队列线程池初始化完成，核心线程数：{}，最大线程数：{}", 
                delayedQueueExecutor.getCorePoolSize(), delayedQueueExecutor.getMaxPoolSize());
    }

    /**
     * 启动线程获取队列
     * @param queueName 队列名称
     * @param redisDelayedQueueListener 任务回调监听
     */
    private <T> void startThread(String queueName, RedisDelayedQueueListener redisDelayedQueueListener) {
        // 使用线程池管理长驻线程
        delayedQueueExecutor.execute(() -> {
            try {
                RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
                redissonClient.getDelayedQueue(blockingFairQueue);
                log.info("启动监听队列线程: {}", queueName);
                
                while (!isShutdown.get() && !Thread.currentThread().isInterrupted()) {
                    try {
                        T t = blockingFairQueue.take();
                        log.info("监听队列线程{},获取到值:{}", queueName, JSON.toJSONString(t));
                        redisDelayedQueueListener.invoke(t);
                    } catch (InterruptedException e) {
                        log.warn("监听队列线程{}被中断", queueName);
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        log.error("队列线程{}执行错误", queueName, e);
                        // 发生异常时短暂休眠，避免疯狂重试
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                log.info("监听队列线程{}已停止", queueName);
            } catch (Exception e) {
                log.error("启动监听队列线程{}失败", queueName, e);
            }
        });
    }
    
    /**
     * 优雅关闭
     */
    @PreDestroy
    public void shutdown() {
        log.info("开始关闭延迟队列监听器...");
        isShutdown.set(true);
        
        if (delayedQueueExecutor != null) {
            try {
                delayedQueueExecutor.shutdown();
                log.info("延迟队列线程池已关闭");
            } catch (Exception e) {
                log.error("关闭延迟队列线程池时发生错误", e);
            }
        }
    }
}
