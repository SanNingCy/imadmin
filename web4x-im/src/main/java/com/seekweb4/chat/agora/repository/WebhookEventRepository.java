package com.seekweb4.chat.agora.repository;

import com.seekweb4.chat.agora.bean.entity.WebhookEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Webhook事件数据访问层接口
 * 
 * <p>提供Webhook事件实体的MongoDB数据访问功能。</p>
 * <p>基于Spring Data MongoDB实现，支持事件记录管理和统计分析查询。</p>
 * 
 * @author Agora
 * @version 1.0
 */
@Repository
public interface WebhookEventRepository extends MongoRepository<WebhookEventEntity, String> {
    
    /**
     * 根据通知ID查找事件记录
     * 
     * @param noticeId 通知ID
     * @return 事件记录
     */
    Optional<WebhookEventEntity> findByNoticeId(String noticeId);
    
    /**
     * 根据事件类型查找事件记录，支持分页
     * 
     * @param eventType 事件类型
     * @param pageable 分页参数
     * @return 事件记录分页结果
     */
    Page<WebhookEventEntity> findByEventType(Integer eventType, Pageable pageable);
    
    /**
     * 根据应用ID查找事件记录，支持分页
     * 
     * @param appId 应用ID
     * @param pageable 分页参数
     * @return 事件记录分页结果
     */
    Page<WebhookEventEntity> findByAppId(String appId, Pageable pageable);
    
    /**
     * 根据应用ID查找事件记录（无分页）
     * 
     * @param appId 应用ID
     * @return 事件记录列表
     */
    List<WebhookEventEntity> findByAppId(String appId);
    
    /**
     * 根据频道名称查找事件记录
     * 
     * @param channelName 频道名称
     * @param pageable 分页参数
     * @return 事件记录分页结果
     */
    Page<WebhookEventEntity> findByChannelName(String channelName, Pageable pageable);
    
    /**
     * 根据用户ID查找事件记录
     * 
     * @param uid 用户ID
     * @param pageable 分页参数
     * @return 事件记录分页结果
     */
    Page<WebhookEventEntity> findByUid(String uid, Pageable pageable);
    
    /**
     * 根据处理状态查找事件记录
     * 
     * @param processStatus 处理状态
     * @return 事件记录列表
     */
    List<WebhookEventEntity> findByProcessStatus(String processStatus);
    
    /**
     * 查找指定时间范围内的事件记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件记录列表
     */
    List<WebhookEventEntity> findByNotifyMsBetween(Long startTime, Long endTime);
    
    /**
     * 查找需要重试的事件记录
     * 
     * @param processStatus 处理状态，通常为"failed"
     * @param currentTime 当前时间
     * @return 需要重试的事件记录列表
     */
    @Query("{'processStatus': ?0, 'retryCount': {'$lt': '$maxRetryCount'}, 'nextRetryTime': {'$lte': ?1}}")
    List<WebhookEventEntity> findEventsForRetry(String processStatus, Long currentTime);
    
    /**
     * 查找处理失败的事件记录
     * 
     * @param appId 应用ID
     * @return 失败的事件记录列表
     */
    @Query("{'appId': ?0, 'processStatus': 'failed'}")
    List<WebhookEventEntity> findFailedEvents(String appId);
    
    /**
     * 统计指定状态的事件数量
     * 
     * @param processStatus 处理状态
     * @return 事件数量
     */
    long countByProcessStatus(String processStatus);
    
    /**
     * 统计指定事件类型的数量
     * 
     * @param eventType 事件类型
     * @return 事件数量
     */
    long countByEventType(Integer eventType);
    
    /**
     * 统计指定应用的事件总数
     * 
     * @param appId 应用ID
     * @return 事件总数
     */
    long countByAppId(String appId);
    
    /**
     * 统计指定时间范围内的事件数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件数量
     */
    long countByNotifyMsBetween(Long startTime, Long endTime);
    
    /**
     * 查找指定频道和用户的事件记录
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param uid 用户ID
     * @param pageable 分页参数
     * @return 事件记录分页结果
     */
    Page<WebhookEventEntity> findByAppIdAndChannelNameAndUid(String appId, String channelName, String uid, Pageable pageable);
    
    /**
     * 查找最近的事件记录
     * 
     * @param limit 限制数量
     * @return 最近的事件记录列表
     */
    @Query(value = "{}", sort = "{'createTime': -1}")
    List<WebhookEventEntity> findRecentEvents(Pageable pageable);
    
    /**
     * 查找处理时间过长的事件记录
     * 
     * @param processTimeThreshold 处理时间阈值（毫秒）
     * @return 处理时间过长的事件记录列表
     */
    @Query("{'processTime': {'$gt': ?0}}")
    List<WebhookEventEntity> findSlowProcessingEvents(Long processTimeThreshold);
    
    /**
     * 统计事件类型分布
     * 
     * @param appId 应用ID
     * @return 事件记录列表，用于统计分析
     */
    @Query(value = "{'appId': ?0}", fields = "{'eventType': 1, '_id': 0}")
    List<WebhookEventEntity> findEventTypeDistribution(String appId);
    
    /**
     * 查找签名验证失败的事件记录
     * 
     * @return 签名验证失败的事件记录列表
     */
    @Query("{'signatureStatus': 'failed'}")
    List<WebhookEventEntity> findSignatureFailedEvents();
    
    /**
     * 查找指定会话的所有事件记录
     * 
     * @param sid 会话ID
     * @return 会话相关的事件记录列表
     */
    List<WebhookEventEntity> findBySid(String sid);
    
    /**
     * 删除指定时间之前的事件记录
     * 
     * @param createTime 创建时间阈值
     */
    void deleteByCreateTimeLessThan(Long createTime);
    
    /**
     * 删除指定应用的所有事件记录
     * 
     * @param appId 应用ID
     */
    void deleteByAppId(String appId);
    
    /**
     * 删除已处理成功且超过保留期的事件记录
     * 
     * @param processStatus 处理状态，通常为"success"
     * @param retentionTime 保留时间阈值
     */
    void deleteByProcessStatusAndCreateTimeLessThan(String processStatus, Long retentionTime);
}