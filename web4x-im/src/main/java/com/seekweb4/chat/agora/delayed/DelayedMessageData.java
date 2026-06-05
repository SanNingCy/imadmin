package com.seekweb4.chat.agora.delayed;

import lombok.Data;
import java.io.Serializable;

/**
 * 延时消息数据对象
 */
@Data
public class DelayedMessageData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 延时秒数
     */
    private Long delaySeconds;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 业务数据
     */
    private String businessData;

    /**
     * 重试次数
     */
    private Integer retryCount = 0;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount = 3;

    public DelayedMessageData() {
        this.createTime = System.currentTimeMillis();
    }

    public DelayedMessageData(String message, String messageType) {
        this();
        this.message = message;
        this.messageType = messageType;
    }

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }

    /**
     * 是否可以重试
     */
    public boolean canRetry() {
        return this.retryCount < this.maxRetryCount;
    }
}
