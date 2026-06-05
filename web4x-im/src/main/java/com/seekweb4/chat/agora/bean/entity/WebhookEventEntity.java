package com.seekweb4.chat.agora.bean.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;

/**
 * Webhook事件记录实体类
 * 
 * <p>用于存储声网RTC Webhook事件的完整记录和处理状态。</p>
 * <p>该实体类对应MongoDB集合，用于持久化webhook事件处理历史。</p>
 * 
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li><b>事件记录</b> - 完整保存webhook事件的原始数据</li>
 *   <li><b>处理状态跟踪</b> - 记录事件处理的状态和结果</li>
 *   <li><b>审计追踪</b> - 提供完整的事件处理审计链</li>
 *   <li><b>统计分析</b> - 支持事件处理的统计分析</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@Document(collection = "webhook_events")
public class WebhookEventEntity {
    
    /**
     * 事件记录唯一标识符
     * 使用noticeId作为主键，确保事件去重
     */
    @MongoId
    private String id;
    
    /**
     * 通知ID
     * 来源于webhook请求中的noticeId字段
     */
    private String noticeId;
    
    /**
     * 产品ID
     */
    private Integer productId;
    
    /**
     * 事件类型
     * 对应ChannelEventTypeEnum中定义的事件类型
     */
    private Integer eventType;
    
    /**
     * 事件类型名称
     */
    private String eventTypeName;
    
    /**
     * 事件发生时间（时间戳，毫秒）
     * 来源于webhook请求中的notifyMs字段
     */
    private Long notifyMs;
    
    /**
     * 会话ID
     */
    private String sid;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 频道名称
     */
    private String channelName;
    
    /**
     * 用户ID
     */
    private String uid;
    
    /**
     * 事件载荷数据
     * 存储webhook请求中的payload完整数据
     */
    private Map<String, Object> payload;
    
    /**
     * 原始请求体
     * 存储完整的webhook请求JSON字符串
     */
    private String rawRequestBody;
    
    /**
     * 请求头信息
     * 存储重要的HTTP请求头信息
     */
    private Map<String, String> requestHeaders;
    
    /**
     * 事件处理状态
     * - pending: 待处理
     * - processing: 处理中
     * - success: 处理成功
     * - failed: 处理失败
     * - ignored: 已忽略（重复事件等）
     */
    private String processStatus;
    
    /**
     * 处理开始时间（时间戳，毫秒）
     */
    private Long processStartTime;
    
    /**
     * 处理完成时间（时间戳，毫秒）
     */
    private Long processEndTime;
    
    /**
     * 处理耗时（毫秒）
     */
    private Long processTime;
    
    /**
     * 处理结果消息
     */
    private String processMessage;
    
    /**
     * 错误信息
     * 当处理失败时记录具体的错误信息
     */
    private String errorMessage;
    
    /**
     * 错误代码
     * 标准化的错误代码
     */
    private String errorCode;
    
    /**
     * 错误堆栈
     */
    private String errorStack;
    
    /**
     * 是否需要重试
     */
    private Boolean needRetry;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;
    
    /**
     * 下次重试时间（时间戳，毫秒）
     */
    private Long nextRetryTime;
    
    /**
     * 签名验证状态
     * - verified: 验证通过
     * - failed: 验证失败
     * - skipped: 跳过验证
     */
    private String signatureStatus;
    
    /**
     * 使用的签名算法
     * - sha1: HMAC-SHA1
     * - sha256: HMAC-SHA256
     */
    private String signatureAlgorithm;
    
    /**
     * 业务处理结果
     * 记录具体业务逻辑处理的结果数据
     */
    private Map<String, Object> businessResult;
    
    /**
     * 关联的频道记录ID
     */
    private String channelRecordId;
    
    /**
     * 关联的用户记录ID
     */
    private String userRecordId;
    
    /**
     * 扩展属性
     */
    private Map<String, Object> properties;
    
    /**
     * 扩展数据（别名）
     * 用于存储处理结果的额外数据
     */
    private Map<String, Object> extData;
    
    /**
     * 事件记录创建时间（时间戳，毫秒）
     */
    private Long createTime;
    
    /**
     * 事件记录更新时间（时间戳，毫秒）
     */
    private Long updateTime;
}