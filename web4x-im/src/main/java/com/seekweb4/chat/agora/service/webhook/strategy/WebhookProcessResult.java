package com.seekweb4.chat.agora.service.webhook.strategy;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * Webhook事件处理结果类
 * 
 * <p>封装webhook事件处理的结果信息，包括处理状态、业务数据和元数据。</p>
 * 
 * @author Agora
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class WebhookProcessResult {
    
    /** 处理状态：success, failed, ignored, pending */
    private String status;
    
    /** 处理消息 */
    private String message;
    
    /** 处理开始时间（毫秒） */
    private Long startTime;
    
    /** 处理结束时间（毫秒） */
    private Long endTime;
    
    /** 处理耗时（毫秒） */
    private Long processTime;
    
    /** 错误信息 */
    private String errorMessage;
    
    /** 错误代码 */
    private String errorCode;
    
    /** 业务结果数据 */
    private Map<String, Object> businessData;
    
    /** 影响的记录ID */
    private Map<String, String> affectedRecordIds;
    
    /** 操作统计 */
    private Map<String, Integer> operationStats;
    
    /** 扩展属性 */
    private Map<String, Object> properties;
    
    /** 是否需要重试 */
    private boolean needRetry;
    
    /** 下次重试时间 */
    private Long nextRetryTime;
    
    public WebhookProcessResult() {
        this.businessData = new HashMap<>();
        this.affectedRecordIds = new HashMap<>();
        this.operationStats = new HashMap<>();
        this.properties = new HashMap<>();
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * 创建成功结果
     */
    public static WebhookProcessResult success(String message) {
        WebhookProcessResult result = new WebhookProcessResult();
        result.setStatus("success")
              .setMessage(message)
              .setEndTime(System.currentTimeMillis());
        result.setProcessTime(result.getEndTime() - result.getStartTime());
        return result;
    }
    
    /**
     * 创建失败结果
     */
    public static WebhookProcessResult failed(String message, String errorCode) {
        WebhookProcessResult result = new WebhookProcessResult();
        result.setStatus("failed")
              .setMessage(message)
              .setErrorMessage(message)
              .setErrorCode(errorCode)
              .setEndTime(System.currentTimeMillis());
        result.setProcessTime(result.getEndTime() - result.getStartTime());
        return result;
    }
    
    /**
     * 创建需要重试的失败结果
     */
    public static WebhookProcessResult failedWithRetry(String message, String errorCode, long nextRetryTime) {
        WebhookProcessResult result = failed(message, errorCode);
        result.setNeedRetry(true)
              .setNextRetryTime(nextRetryTime);
        return result;
    }
    
    /**
     * 创建忽略结果
     */
    public static WebhookProcessResult ignored(String message) {
        WebhookProcessResult result = new WebhookProcessResult();
        result.setStatus("ignored")
              .setMessage(message)
              .setEndTime(System.currentTimeMillis());
        result.setProcessTime(result.getEndTime() - result.getStartTime());
        return result;
    }
    
    /**
     * 添加业务数据
     */
    public WebhookProcessResult addBusinessData(String key, Object value) {
        if (this.businessData == null) {
            this.businessData = new HashMap<>();
        }
        this.businessData.put(key, value);
        return this;
    }
    
    /**
     * 添加影响的记录ID
     */
    public WebhookProcessResult addAffectedRecordId(String recordType, String recordId) {
        if (this.affectedRecordIds == null) {
            this.affectedRecordIds = new HashMap<>();
        }
        this.affectedRecordIds.put(recordType, recordId);
        return this;
    }
    
    /**
     * 添加操作统计
     */
    public WebhookProcessResult addOperationStat(String operation, int count) {
        if (this.operationStats == null) {
            this.operationStats = new HashMap<>();
        }
        this.operationStats.put(operation, count);
        return this;
    }
    
    /**
     * 完成处理并计算耗时
     */
    public WebhookProcessResult complete() {
        this.endTime = System.currentTimeMillis();
        this.processTime = this.endTime - this.startTime;
        return this;
    }
    
    /**
     * 判断处理是否成功
     */
    public boolean isSuccess() {
        return "success".equals(this.status);
    }
    
    /**
     * 判断是否失败
     */
    public boolean isFailed() {
        return "failed".equals(this.status);
    }
    
    /**
     * 判断是否被忽略
     */
    public boolean isIgnored() {
        return "ignored".equals(this.status);
    }
}