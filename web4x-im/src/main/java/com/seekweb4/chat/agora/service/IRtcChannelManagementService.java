package com.seekweb4.chat.agora.service;

import com.seekweb4.chat.agora.bean.entity.RtcChannelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * RTC频道管理服务接口
 * 
 * <p>提供RTC频道的完整管理功能，包括频道的创建、查询、更新和删除等操作。</p>
 * <p>集成声网API和本地数据存储，提供统一的频道管理服务。</p>
 * 
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li><b>频道CRUD操作</b> - 创建、读取、更新、删除频道记录</li>
 *   <li><b>频道状态管理</b> - 跟踪频道活跃状态和用户数量</li>
 *   <li><b>API集成</b> - 与声网RTC API进行数据同步</li>
 *   <li><b>统计分析</b> - 提供频道使用统计和分析功能</li>
 *   <li><b>封禁规则管理</b> - 用户权限封禁规则的增删改查</li>
 *   <li><b>项目管理</b> - Agora项目的创建和配置管理</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 */
public interface IRtcChannelManagementService {
    
    // ==================== 频道基础管理 ====================
    
    /**
     * 创建或更新频道记录
     * 
     * <p>根据应用ID和频道名称创建新的频道记录，如果频道已存在则更新相关信息。</p>
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param ownerId 频道所有者用户ID
     * @param channelMode 频道模式（live/communication）
     * @param description 频道描述
     * @return 频道实体
     */
    RtcChannelEntity createOrUpdateChannel(String appId, String channelName, String ownerId, 
                                         String channelMode, String description);
    
    /**
     * 根据应用ID和频道名称查找频道
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @return 频道实体
     */
    Optional<RtcChannelEntity> findChannel(String appId, String channelName);
    
    /**
     * 获取频道详细信息
     * 
     * <p>返回频道的完整信息，包括当前用户列表、统计数据等。</p>
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @return 频道详细信息
     */
    RtcChannelEntity getChannelDetails(String appId, String channelName);
    
    /**
     * 更新频道状态
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param status 新状态
     * @return 更新后的频道实体
     */
    RtcChannelEntity updateChannelStatus(String appId, String channelName, String status);
    
    /**
     * 删除频道记录
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @return 是否删除成功
     */
    boolean deleteChannel(String appId, String channelName);
    
    // ==================== 频道查询功能 ====================
    
    /**
     * 获取应用的频道列表（分页）
     * 
     * @param appId 应用ID
     * @param pageable 分页参数
     * @return 频道列表分页结果
     */
    Page<RtcChannelEntity> getChannelList(String appId, Pageable pageable);
    
    /**
     * 获取活跃频道列表
     * 
     * <p>返回当前有用户在线的频道列表。</p>
     * 
     * @param appId 应用ID
     * @return 活跃频道列表
     */
    List<RtcChannelEntity> getActiveChannels(String appId);
    
    /**
     * 根据状态获取频道列表
     * 
     * @param appId 应用ID
     * @param status 频道状态
     * @return 频道列表
     */
    List<RtcChannelEntity> getChannelsByStatus(String appId, String status);
    
    /**
     * 根据频道模式获取频道列表
     * 
     * @param channelMode 频道模式
     * @param pageable 分页参数
     * @return 频道列表分页结果
     */
    Page<RtcChannelEntity> getChannelsByMode(String channelMode, Pageable pageable);
    
    /**
     * 获取用户拥有的频道列表
     * 
     * @param ownerId 所有者用户ID
     * @return 频道列表
     */
    List<RtcChannelEntity> getChannelsByOwner(String ownerId);
    
    // ==================== 用户管理功能 ====================
    
    /**
     * 添加用户到频道
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param uid 用户ID
     * @return 更新后的频道实体
     */
    RtcChannelEntity addUserToChannel(String appId, String channelName, String uid);
    
    /**
     * 添加用户到频道并同步验证API状态
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param uid 用户ID
     * @param basicAuth Basic认证字符串（可选）
     * @return 更新后的频道实体
     */
    RtcChannelEntity addUserToChannelWithSync(String appId, String channelName, String uid, String basicAuth);
    
    /**
     * 从频道移除用户
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param uid 用户ID
     * @return 更新后的频道实体
     */
    RtcChannelEntity removeUserFromChannel(String appId, String channelName, String uid);
    
    /**
     * 更新频道用户数量
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param userCount 新的用户数量
     * @return 更新后的频道实体
     */
    RtcChannelEntity updateUserCount(String appId, String channelName, Integer userCount);
    
    // ==================== API集成功能 ====================
    
    /**
     * 从声网API同步频道列表
     * 
     * <p>从声网RTC API获取最新的频道列表并更新本地数据库。</p>
     * 
     * @param appId 应用ID
     * @param basicAuth 认证字符串
     * @return 同步的频道数量
     */
    int syncChannelsFromAPI(String appId, String basicAuth);
    
    /**
     * 从声网API同步单个频道信息
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param basicAuth 认证字符串
     * @return 同步后的频道实体
     */
    RtcChannelEntity syncChannelFromAPI(String appId, String channelName, String basicAuth);
    
    /**
     * 从声网API同步频道用户列表
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param basicAuth 认证字符串
     * @return 更新后的频道实体
     */
    RtcChannelEntity syncChannelUsersFromAPI(String appId, String channelName, String basicAuth);
    
    // ==================== 统计分析功能 ====================
    
    /**
     * 获取应用的频道统计信息
     * 
     * @param appId 应用ID
     * @return 统计信息Map
     */
    java.util.Map<String, Object> getChannelStatistics(String appId);
    
    /**
     * 获取频道使用趋势数据
     * 
     * @param appId 应用ID
     * @param days 统计天数
     * @return 趋势数据列表
     */
    List<java.util.Map<String, Object>> getChannelUsageTrend(String appId, int days);
    
    /**
     * 获取热门频道排行
     * 
     * @param appId 应用ID
     * @param limit 返回数量限制
     * @return 热门频道列表
     */
    List<RtcChannelEntity> getPopularChannels(String appId, int limit);
    
    // ==================== 维护功能 ====================
    
    /**
     * 清理非活跃频道
     * 
     * <p>删除长时间没有用户活动的频道记录。</p>
     * 
     * @param inactiveThresholdHours 非活跃时间阈值（小时）
     * @return 清理的频道数量
     */
    int cleanupInactiveChannels(int inactiveThresholdHours);
    
    /**
     * 清理过期频道数据
     * 
     * @param retentionDays 数据保留天数
     * @return 清理的频道数量
     */
    int cleanupExpiredChannels(int retentionDays);
    
    /**
     * 修复频道数据一致性
     * 
     * <p>检查并修复频道数据的一致性问题。</p>
     * 
     * @param appId 应用ID
     * @return 修复的记录数量
     */
    int repairChannelDataConsistency(String appId);
}