package com.seekweb4.chat.agora.delayed;

import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 延时消息服务类
 * 提供业务层面的延时消息操作方法
 */
@Service
@Slf4j
public class DelayedMessageService {

    @Autowired
    private RedisDelayedQueue redisDelayedQueue;

    /**
     * 发送自定义延时消息
     */
    public void sendCustomDelayedMessage(DelayedMessageData messageData,long delay, TimeUnit timeUnit) {
        // 根据时间单位选择不同的方法
        switch (timeUnit) {
            case SECONDS:
                redisDelayedQueue.addQueueSeconds(messageData, delay, DelayedMessageListener.class);
                break;
            case MINUTES:
                redisDelayedQueue.addQueueMinutes(messageData, delay, DelayedMessageListener.class);
                break;
            case HOURS:
                redisDelayedQueue.addQueueHours(messageData, delay, DelayedMessageListener.class);
                break;
            case DAYS:
                redisDelayedQueue.addQueueDays(messageData, delay, DelayedMessageListener.class);
                break;
            default:
                throw new IllegalArgumentException("不支持的时间单位: " + timeUnit);
        }
        log.info("自定义延时消息已加入队列: message={}, type={}, delay={} {}",
                messageData.getBusinessData(), messageData.getMessageType(), delay, timeUnit);
    }

    /**
     * 取消延时消息
     */
    public void cancelDelayedMessage(DelayedMessageData message) {
        redisDelayedQueue.removeDelayedQueue(message, DelayedMessageListener.class);
        log.info("延时消息已取消: message={}", message);
    }
}
