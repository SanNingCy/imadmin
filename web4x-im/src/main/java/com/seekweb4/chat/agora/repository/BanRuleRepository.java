package com.seekweb4.chat.agora.repository;

import com.seekweb4.chat.agora.bean.entity.BanRuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 封禁规则数据访问层接口
 * 
 * <p>提供封禁规则实体的MongoDB数据访问功能。</p>
 * <p>基于Spring Data MongoDB实现，支持规则管理和权限控制查询。</p>
 * 
 * @author Agora
 * @version 1.0
 */
@Repository
public interface BanRuleRepository extends MongoRepository<BanRuleEntity, String> {
    
    /**
     * 根据声网规则ID查找封禁规则
     * 
     * @param ruleId 声网返回的规则ID
     * @return 封禁规则实体
     */
    Optional<BanRuleEntity> findByRuleId(Long ruleId);
    
    /**
     * 根据应用ID查找所有封禁规则，支持分页
     * 
     * @param appId 应用ID
     * @param pageable 分页参数
     * @return 封禁规则分页结果
     */
    Page<BanRuleEntity> findByAppId(String appId, Pageable pageable);
    
    /**
     * 根据状态查找封禁规则
     * 
     * @param status 规则状态
     * @return 封禁规则列表
     */
    List<BanRuleEntity> findByStatus(String status);
    
    /**
     * 根据应用ID和状态查找封禁规则
     * 
     * @param appId 应用ID
     * @param status 规则状态
     * @return 封禁规则列表
     */
    List<BanRuleEntity> findByAppIdAndStatus(String appId, String status);
    
    /**
     * 根据用户ID查找相关的封禁规则
     * 
     * @param uid 用户ID
     * @return 封禁规则列表
     */
    List<BanRuleEntity> findByUid(String uid);
    
    /**
     * 根据频道名称查找相关的封禁规则
     * 
     * @param channelName 频道名称
     * @return 封禁规则列表
     */
    List<BanRuleEntity> findByChannelName(String channelName);
    
    /**
     * 根据IP地址查找相关的封禁规则
     * 
     * @param ipAddress IP地址
     * @return 封禁规则列表
     */
    List<BanRuleEntity> findByIpAddress(String ipAddress);
    
    /**
     * 查找即将过期的封禁规则
     * 
     * @param expireTimeThreshold 过期时间阈值
     * @param status 规则状态，通常为"active"
     * @return 即将过期的封禁规则列表
     */
    List<BanRuleEntity> findByExpireTimeLessThanAndStatus(Long expireTimeThreshold, String status);
    
    /**
     * 查找已过期但未更新状态的封禁规则
     * 
     * @param currentTime 当前时间
     * @param status 规则状态，通常为"active"
     * @return 已过期的封禁规则列表
     */
    List<BanRuleEntity> findByExpireTimeLessThanAndStatusNot(Long currentTime, String status);
    
    /**
     * 查找永久封禁规则
     * 
     * @return 永久封禁规则列表
     */
    @Query("{'expireTime': null, 'status': 'active'}")
    List<BanRuleEntity> findPermanentBanRules();
    
    /**
     * 根据封禁类型查找规则
     * 
     * @param banType 封禁类型
     * @param pageable 分页参数
     * @return 封禁规则分页结果
     */
    Page<BanRuleEntity> findByBanType(String banType, Pageable pageable);
    
    /**
     * 根据严重级别查找规则
     * 
     * @param severity 严重级别
     * @return 封禁规则列表
     */
    List<BanRuleEntity> findBySeverity(String severity);
    
    /**
     * 根据创建者查找规则
     * 
     * @param createdBy 创建者
     * @param pageable 分页参数
     * @return 封禁规则分页结果
     */
    Page<BanRuleEntity> findByCreatedBy(String createdBy, Pageable pageable);
    
    /**
     * 查找指定时间范围内创建的规则
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 封禁规则列表
     */
    List<BanRuleEntity> findByCreateTimeBetween(Long startTime, Long endTime);
    
    /**
     * 统计指定应用的封禁规则数量
     * 
     * @param appId 应用ID
     * @return 规则数量
     */
    long countByAppId(String appId);
    
    /**
     * 统计指定状态的封禁规则数量
     * 
     * @param status 规则状态
     * @return 规则数量
     */
    long countByStatus(String status);
    
    /**
     * 统计指定用户相关的封禁规则数量
     * 
     * @param uid 用户ID
     * @param status 规则状态
     * @return 规则数量
     */
    long countByUidAndStatus(String uid, String status);
    
    /**
     * 查找活跃的用户封禁规则
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param uid 用户ID
     * @return 活跃的封禁规则列表
     */
    @Query("{'appId': ?0, '$or': [{'channelName': ?1}, {'channelName': null}], '$or': [{'uid': ?2}, {'uid': null}], 'status': 'active'}")
    List<BanRuleEntity> findActiveRulesForUser(String appId, String channelName, String uid);
    
    /**
     * 查找包含特定权限的封禁规则
     * 
     * @param privilege 权限名称
     * @param status 规则状态
     * @return 封禁规则列表
     */
    @Query("{'privileges': ?0, 'status': ?1}")
    List<BanRuleEntity> findByPrivilegeAndStatus(String privilege, String status);
    
    /**
     * 查找执行次数较高的规则
     * 
     * @param executeCountThreshold 执行次数阈值
     * @return 高频执行的封禁规则列表
     */
    @Query("{'executeCount': {'$gte': ?0}}")
    List<BanRuleEntity> findHighFrequencyRules(Integer executeCountThreshold);
    
    /**
     * 查找需要自动解封的规则
     * 
     * @param currentTime 当前时间
     * @return 需要自动解封的规则列表
     */
    @Query("{'autoUnban': true, 'expireTime': {'$lte': ?0}, 'status': 'active'}")
    List<BanRuleEntity> findRulesForAutoUnban(Long currentTime);
    
    /**
     * 删除指定时间之前创建的已失效规则
     * 
     * @param createTime 创建时间阈值
     * @param status 规则状态，通常为"expired"或"cancelled"
     */
    void deleteByCreateTimeLessThanAndStatus(Long createTime, String status);
    
    /**
     * 删除指定应用的所有封禁规则
     * 
     * @param appId 应用ID
     */
    void deleteByAppId(String appId);
}