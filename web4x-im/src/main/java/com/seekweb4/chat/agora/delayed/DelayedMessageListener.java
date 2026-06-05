package com.seekweb4.chat.agora.delayed;

import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueueListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延时消息监听器
 */
@Component
@Slf4j
public class DelayedMessageListener implements RedisDelayedQueueListener<DelayedMessageData> {

    @Autowired
    private RedisDelayedQueue redisDelayedQueue;

    @Autowired
    private MeetingDelayedService meetingDelayedService;

    @Override
    public void invoke(DelayedMessageData messageData) {
        try {
            log.info("开始处理延时消息: {}", messageData);

            // 根据消息类型进行不同的处理
            switch (messageData.getMessageType()) {
                case "MeetingDelayedClose":
                    meetingDelayedService.handleMeetingClose(messageData);
                    break;
                default:
                    handleDefault(messageData);
                    break;
            }
            log.info("延时消息处理完成: {}", messageData.getId());
        } catch (Exception e) {
            log.error("处理延时消息失败: {}", messageData, e);
            handleMessageFailure(messageData, e);
        }
    }

    /**
     * 处理会议开启消息
     */
    private void handleMeetingOpen(DelayedMessageData messageData) {

    }

    /**
     * 处理会议结束消息
     */
    private void handleMeetingClose(DelayedMessageData messageData) {

    }

    /**
     * 处理默认消息
     */
    private void handleDefault(DelayedMessageData messageData) {
        log.info("处理默认消息: {}", messageData.getMessage());
    }

    /**
     * 处理消息失败
     */
    private void handleMessageFailure(DelayedMessageData messageData, Exception e) {
        if (messageData.canRetry()) {
            log.warn("消息处理失败，准备重试: {}, 重试次数: {}/{}",
                    messageData.getId(), messageData.getRetryCount() + 1, messageData.getMaxRetryCount());

            // 增加重试次数
            messageData.incrementRetryCount();

            // 重新加入延时队列，延时30秒后重试
            try {
                redisDelayedQueue.addQueueSeconds(messageData, 30, DelayedMessageListener.class);
                log.info("消息已重新加入延时队列: {}", messageData.getId());
            } catch (Exception retryException) {
                log.error("重新加入延时队列失败: {}", messageData.getId(), retryException);
            }
        } else {
            log.error("消息处理失败，已达到最大重试次数，放弃处理: {}", messageData.getId());
            // 这里可以将失败的消息记录到数据库或发送到死信队列
            // deadLetterQueueService.addToDeadLetterQueue(messageData, e);
        }
    }
}
