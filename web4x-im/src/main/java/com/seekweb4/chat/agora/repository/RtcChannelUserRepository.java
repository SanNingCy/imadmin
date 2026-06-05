package com.seekweb4.chat.agora.repository;

import com.seekweb4.chat.agora.bean.entity.RtcChannelUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RTC频道用户数据访问层接口
 * 
 * <p>提供RTC频道用户实体的MongoDB数据访问功能。</p>
 * <p>基于Spring Data MongoDB实现，支持用户会话管理和行为统计查询。</p>
 * 
 * @author Agora
 * @version 1.0
 */
@Repository
public interface RtcChannelUserRepository extends MongoRepository<RtcChannelUserEntity, String> {
    
    /**
     * 根据应用ID、频道名称和用户ID查找当前会话
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param uid 用户ID
     * @param status 用户状态，通常查找"online"状态
     * @return 用户会话实体
     */
    Optional<RtcChannelUserEntity> findByAppIdAndChannelNameAndUidAndStatus(String appId, String channelName, String uid, String status);
    
    /**
     * 根据应用ID和频道名称查找所有在线用户
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param status 用户状态，通常为"online"
     * @return 在线用户列表
     */
    List<RtcChannelUserEntity> findByAppIdAndChannelNameAndStatus(String appId, String channelName, String status);
    
    /**
     * 根据用户ID查找其所有会话记录，支持分页
     * 
     * @param uid 用户ID
     * @param pageable 分页参数
     * @return 用户会话记录分页结果
     */
    Page<RtcChannelUserEntity> findByUid(String uid, Pageable pageable);
    
    /**
     * 根据用户ID和状态查找会话记录
     * 
     * @param uid 用户ID
     * @param status 状态
     * @return 用户会话记录列表
     */
    List<RtcChannelUserEntity> findByUidAndStatus(String uid, String status);
    
    /**
     * 根据频道查找所有用户记录，支持分页
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param pageable 分页参数
     * @return 用户记录分页结果
     */
    Page<RtcChannelUserEntity> findByAppIdAndChannelName(String appId, String channelName, Pageable pageable);
    
    /**
     * 根据频道查找所有用户记录（无分页）
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @return 用户记录列表
     */
    List<RtcChannelUserEntity> findByAppIdAndChannelName(String appId, String channelName);
    
    /**
     * 根据角色查找用户
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param role 用户角色
     * @param status 用户状态
     * @return 指定角色的用户列表
     */
    List<RtcChannelUserEntity> findByAppIdAndChannelNameAndRoleAndStatus(String appId, String channelName, String role, String status);
    
    /**
     * 查找指定时间范围内的用户记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户记录列表
     */
    List<RtcChannelUserEntity> findByJoinTimeBetween(Long startTime, Long endTime);
    
    /**
     * 查找长时间在线的用户会话
     * 
     * @param joinTimeThreshold 加入时间阈值
     * @param status 用户状态
     * @return 长时间在线用户列表
     */
    List<RtcChannelUserEntity> findByJoinTimeLessThanAndStatus(Long joinTimeThreshold, String status);
    
    /**
     * 查找已离开频道的用户记录
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @return 已离开用户记录列表
     */
    List<RtcChannelUserEntity> findByAppIdAndChannelNameAndLeaveTimeIsNotNull(String appId, String channelName);
    
    /**
     * 根据IP地址查找用户记录
     * 
     * @param ipAddress IP地址
     * @return 用户记录列表
     */
    List<RtcChannelUserEntity> findByIpAddress(String ipAddress);
    
    /**
     * 根据设备信息查找用户记录
     * 
     * @param deviceInfo 设备信息
     * @return 用户记录列表
     */
    List<RtcChannelUserEntity> findByDeviceInfo(String deviceInfo);
    
    /**
     * 统计指定频道的用户数量
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param status 用户状态
     * @return 用户数量
     */
    long countByAppIdAndChannelNameAndStatus(String appId, String channelName, String status);
    
    /**
     * 统计指定用户的会话总数
     * 
     * @param uid 用户ID
     * @return 会话总数
     */
    long countByUid(String uid);
    
    /**
     * 统计指定应用的用户总数
     * 
     * @param appId 应用ID
     * @return 用户总数
     */
    long countByAppId(String appId);
    
    /**
     * 根据应用ID查找所有用户记录，支持分页
     * 
     * @param appId 应用ID
     * @param pageable 分页参数
     * @return 用户记录分页结果
     */
    Page<RtcChannelUserEntity> findByAppId(String appId, Pageable pageable);
    
    /**
     * 查找频道中的主播用户
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param role 角色，通常为"broadcaster"
     * @param status 用户状态
     * @return 主播用户列表
     */
    @Query("{'appId': ?0, 'channelName': ?1, 'role': ?2, 'status': ?3}")
    List<RtcChannelUserEntity> findBroadcastersInChannel(String appId, String channelName, String role, String status);
    
    /**
     * 查找需要清理的过期用户记录
     * 
     * @param thresholdTime 清理时间阈值
     * @return 需要清理的用户记录列表
     */
    @Query("{'status': 'offline', 'leaveTime': {'$lt': ?0}}")
    List<RtcChannelUserEntity> findExpiredUserRecords(Long thresholdTime);
    
    /**
     * 查找用户在所有频道的活跃会话
     * 
     * @param uid 用户ID
     * @param status 状态，通常为"online"
     * @return 用户活跃会话列表
     */
    @Query("{'uid': ?0, 'status': ?1}")
    List<RtcChannelUserEntity> findUserActiveSessions(String uid, String status);
    
    /**
     * 统计用户在指定时间范围内的会话时长
     * 
     * @param uid 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 会话记录列表，用于计算总时长
     */
    @Query("{'uid': ?0, 'joinTime': {'$gte': ?1, '$lte': ?2}, 'duration': {'$exists': true}}")
    List<RtcChannelUserEntity> findUserSessionsInTimeRange(String uid, Long startTime, Long endTime);
    
    /**
     * 查找频道用户活跃度统计
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param activeThreshold 活跃时间阈值
     * @return 活跃用户列表
     */
    @Query("{'appId': ?0, 'channelName': ?1, 'lastActiveTime': {'$gte': ?2}}")
    List<RtcChannelUserEntity> findActiveUsersInChannel(String appId, String channelName, Long activeThreshold);
    
    /**
     * 删除指定时间之前的离线用户记录
     * 
     * @param leaveTime 离开时间阈值
     */
    void deleteByLeaveTimeLessThan(Long leaveTime);
    
    /**
     * 查找用户在指定频道的最新活跃会话
     * 
     * @param appId 应用ID
     * @param channelName 频道名称  
     * @param uid 用户ID
     * @return 最新活跃会话记录
     */
    @Query(value = "{'appId': ?0, 'channelName': ?1, 'uid': ?2, 'status': 'online'}", sort = "{'joinTime': -1}")
    Optional<RtcChannelUserEntity> findLatestActiveUserSession(String appId, String channelName, String uid);
    
    /**
     * 删除指定频道的所有用户记录
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     */
    void deleteByAppIdAndChannelName(String appId, String channelName);
}