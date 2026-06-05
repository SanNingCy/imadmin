package com.seekweb4.chat.agora.repository;

import com.seekweb4.chat.agora.bean.entity.RtcChannelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RTC频道数据访问层接口
 * 
 * <p>提供RTC频道实体的MongoDB数据访问功能。</p>
 * <p>基于Spring Data MongoDB实现，提供基础CRUD操作和自定义查询方法。</p>
 * 
 * @author Agora
 * @version 1.0
 */
@Repository
public interface RtcChannelRepository extends MongoRepository<RtcChannelEntity, String> {
    
    /**
     * 根据应用ID和频道名称查找频道
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @return 频道实体，如果不存在则返回Optional.empty()
     */
    Optional<RtcChannelEntity> findByAppIdAndChannelName(String appId, String channelName);
    
    /**
     * 根据应用ID查找所有频道，支持分页
     * 
     * @param appId 应用ID
     * @param pageable 分页参数
     * @return 频道列表分页结果
     */
    Page<RtcChannelEntity> findByAppId(String appId, Pageable pageable);
    
    /**
     * 根据状态查找频道
     * 
     * @param status 频道状态
     * @return 频道列表
     */
    List<RtcChannelEntity> findByStatus(String status);
    
    /**
     * 根据应用ID和状态查找频道
     * 
     * @param appId 应用ID
     * @param status 频道状态
     * @return 频道列表
     */
    List<RtcChannelEntity> findByAppIdAndStatus(String appId, String status);
    
    /**
     * 查找指定时间之前创建的频道
     * 
     * @param createTime 创建时间阈值
     * @return 频道列表
     */
    List<RtcChannelEntity> findByCreateTimeLessThan(Long createTime);
    
    /**
     * 查找最后活跃时间在指定时间之前的频道
     * 
     * @param lastActiveTime 最后活跃时间阈值
     * @return 频道列表
     */
    List<RtcChannelEntity> findByLastActiveTimeLessThan(Long lastActiveTime);
    
    /**
     * 根据用户ID查找包含该用户的频道
     * 
     * @param uid 用户ID
     * @return 频道列表
     */
    List<RtcChannelEntity> findByCurrentUserIdsContaining(String uid);
    
    /**
     * 查找在线用户数大于指定数量的频道
     * 
     * @param userCount 用户数阈值
     * @return 频道列表
     */
    List<RtcChannelEntity> findByCurrentUserCountGreaterThan(Integer userCount);
    
    /**
     * 根据频道模式查找频道
     * 
     * @param channelMode 频道模式
     * @param pageable 分页参数
     * @return 频道列表分页结果
     */
    Page<RtcChannelEntity> findByChannelMode(String channelMode, Pageable pageable);
    
    /**
     * 根据所有者ID查找频道
     * 
     * @param ownerId 所有者用户ID
     * @return 频道列表
     */
    List<RtcChannelEntity> findByOwnerId(String ownerId);
    
    /**
     * 统计指定应用的频道总数
     * 
     * @param appId 应用ID
     * @return 频道数量
     */
    long countByAppId(String appId);
    
    /**
     * 统计指定状态的频道数量
     * 
     * @param status 频道状态
     * @return 频道数量
     */
    long countByStatus(String status);
    
    /**
     * 统计指定应用和状态的频道数量
     * 
     * @param appId 应用ID
     * @param status 频道状态
     * @return 频道数量
     */
    long countByAppIdAndStatus(String appId, String status);
    
    /**
     * 查找活跃频道（有在线用户）
     * 
     * @param appId 应用ID
     * @return 活跃频道列表
     */
    @Query("{'appId': ?0, 'currentUserCount': {'$gt': 0}}")
    List<RtcChannelEntity> findActiveChannels(String appId);
    
    /**
     * 查找需要清理的非活跃频道
     * 
     * @param inactiveThreshold 非活跃时间阈值
     * @param status 频道状态
     * @return 需要清理的频道列表
     */
    @Query("{'lastActiveTime': {'$lt': ?0}, 'status': ?1}")
    List<RtcChannelEntity> findChannelsForCleanup(Long inactiveThreshold, String status);
    
    /**
     * 批量删除指定时间之前的频道
     * 
     * @param createTime 创建时间阈值
     */
    void deleteByCreateTimeLessThan(Long createTime);
    
    /**
     * 删除指定应用的所有频道
     * 
     * @param appId 应用ID
     */
    void deleteByAppId(String appId);
}