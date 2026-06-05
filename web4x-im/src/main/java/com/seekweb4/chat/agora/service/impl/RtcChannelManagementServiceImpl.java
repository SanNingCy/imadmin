package com.seekweb4.chat.agora.service.impl;

import com.seekweb4.chat.agora.bean.dto.BanRuleResponseDto;
import com.seekweb4.chat.agora.bean.dto.UserListResponseDto;
import com.seekweb4.chat.agora.service.api.response.BanRuleListResponse;
import com.seekweb4.chat.agora.service.api.response.BanRuleResponse;
import com.seekweb4.chat.agora.service.api.response.ChannelInfo;
import com.seekweb4.chat.agora.service.api.response.ChannelListResponse;
import com.seekweb4.chat.agora.bean.entity.RtcChannelEntity;
import com.seekweb4.chat.agora.repository.RtcChannelRepository;
import com.seekweb4.chat.agora.service.IRtcChannelManagementService;
import com.seekweb4.chat.agora.service.api.RtcChannelAPIService;
import com.seekweb4.chat.agora.service.api.request.BanRuleCreateRequest;
import com.seekweb4.chat.agora.service.api.request.BanRuleUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * RTC频道管理服务实现类
 * 
 * <p>RTC频道管理服务的具体实现，集成声网API和MongoDB数据存储。</p>
 * <p>提供完整的频道生命周期管理和数据统计功能。</p>
 * 
 * @author Agora
 * @version 1.0
 */
@Slf4j
@Service
public class RtcChannelManagementServiceImpl implements IRtcChannelManagementService {
    
    @Resource
    private RtcChannelRepository channelRepository;
    
    @Resource
    private RtcChannelAPIService rtcChannelAPIService;
    
    @Override
    @Transactional
    public RtcChannelEntity createOrUpdateChannel(String appId, String channelName, String ownerId, 
                                                String channelMode, String description) {
        log.info("创建或更新频道 - appId: {}, channelName: {}, ownerId: {}", appId, channelName, ownerId);
        
        // 生成频道ID
        String channelId = generateChannelId(appId, channelName);
        
        // 查找现有频道
        Optional<RtcChannelEntity> existingChannel = channelRepository.findByAppIdAndChannelName(appId, channelName);
        
        RtcChannelEntity channel;
        Long currentTime = System.currentTimeMillis();
        
        if (existingChannel.isPresent()) {
            // 更新现有频道
            channel = existingChannel.get();
            channel.setUpdateTime(currentTime);
            channel.setLastActiveTime(currentTime);
            if (description != null) {
                channel.setDescription(description);
            }
            if (ownerId != null) {
                channel.setOwnerId(ownerId);
            }
            if (channelMode != null) {
                channel.setChannelMode(channelMode);
            }
            log.info("更新现有频道 - channelId: {}", channelId);
        } else {
            // 创建新频道
            channel = new RtcChannelEntity();
            channel.setId(channelId);
            channel.setAppId(appId);
            channel.setChannelName(channelName);
            channel.setOwnerId(ownerId);
            channel.setChannelMode(channelMode != null ? channelMode : "communication");
            channel.setDescription(description);
            channel.setStatus("active");
            channel.setCurrentUserCount(0);
            channel.setMaxUserCount(0);
            channel.setTotalUserCount(0);
            channel.setCurrentUserIds(new ArrayList<>());
            channel.setCreateTime(currentTime);
            channel.setUpdateTime(currentTime);
            channel.setLastActiveTime(currentTime);
            channel.setProperties(new HashMap<>());
            channel.setConfig(new HashMap<>());
            log.info("创建新频道 - channelId: {}", channelId);
        }
        
        return channelRepository.save(channel);
    }
    
    @Override
    public Optional<RtcChannelEntity> findChannel(String appId, String channelName) {
        log.debug("查找频道 - appId: {}, channelName: {}", appId, channelName);
        return channelRepository.findByAppIdAndChannelName(appId, channelName);
    }
    
    @Override
    public RtcChannelEntity getChannelDetails(String appId, String channelName) {
        log.info("获取频道详细信息 - appId: {}, channelName: {}", appId, channelName);
        
        Optional<RtcChannelEntity> channelOpt = findChannel(appId, channelName);
        if (channelOpt.isPresent()) {
            RtcChannelEntity channel = channelOpt.get();
            // 更新最后访问时间
            channel.setLastActiveTime(System.currentTimeMillis());
            return channelRepository.save(channel);
        } else {
            log.warn("频道不存在 - appId: {}, channelName: {}", appId, channelName);
            throw new RuntimeException("频道不存在");
        }
    }
    
    @Override
    @Transactional
    public RtcChannelEntity updateChannelStatus(String appId, String channelName, String status) {
        log.info("更新频道状态 - appId: {}, channelName: {}, status: {}", appId, channelName, status);
        
        Optional<RtcChannelEntity> channelOpt = findChannel(appId, channelName);
        if (channelOpt.isPresent()) {
            RtcChannelEntity channel = channelOpt.get();
            channel.setStatus(status);
            channel.setUpdateTime(System.currentTimeMillis());
            return channelRepository.save(channel);
        } else {
            throw new RuntimeException("频道不存在");
        }
    }
    
    @Override
    @Transactional
    public boolean deleteChannel(String appId, String channelName) {
        log.info("删除频道 - appId: {}, channelName: {}", appId, channelName);
        
        Optional<RtcChannelEntity> channelOpt = findChannel(appId, channelName);
        if (channelOpt.isPresent()) {
            channelRepository.delete(channelOpt.get());
            log.info("频道删除成功 - appId: {}, channelName: {}", appId, channelName);
            return true;
        } else {
            log.warn("频道不存在，无法删除 - appId: {}, channelName: {}", appId, channelName);
            return false;
        }
    }
    
    @Override
    public Page<RtcChannelEntity> getChannelList(String appId, Pageable pageable) {
        log.debug("获取频道列表 - appId: {}, page: {}, size: {}", appId, pageable.getPageNumber(), pageable.getPageSize());
        
        // 添加默认排序：按更新时间倒序
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                                    Sort.by(Sort.Direction.DESC, "updateTime"));
        }
        
        return channelRepository.findByAppId(appId, pageable);
    }
    
    @Override
    public List<RtcChannelEntity> getActiveChannels(String appId) {
        log.debug("获取活跃频道列表 - appId: {}", appId);
        return channelRepository.findActiveChannels(appId);
    }
    
    @Override
    public List<RtcChannelEntity> getChannelsByStatus(String appId, String status) {
        log.debug("根据状态获取频道列表 - appId: {}, status: {}", appId, status);
        return channelRepository.findByAppIdAndStatus(appId, status);
    }
    
    @Override
    public Page<RtcChannelEntity> getChannelsByMode(String channelMode, Pageable pageable) {
        log.debug("根据模式获取频道列表 - channelMode: {}", channelMode);
        return channelRepository.findByChannelMode(channelMode, pageable);
    }
    
    @Override
    public List<RtcChannelEntity> getChannelsByOwner(String ownerId) {
        log.debug("获取用户拥有的频道列表 - ownerId: {}", ownerId);
        return channelRepository.findByOwnerId(ownerId);
    }
    
    @Override
    @Transactional
    public RtcChannelEntity addUserToChannel(String appId, String channelName, String uid) {
        log.info("添加用户到频道 - appId: {}, channelName: {}, uid: {}", appId, channelName, uid);
        
        Optional<RtcChannelEntity> channelOpt = findChannel(appId, channelName);
        if (channelOpt.isPresent()) {
            RtcChannelEntity channel = channelOpt.get();
            List<String> currentUsers = channel.getCurrentUserIds();
            if (currentUsers == null) {
                currentUsers = new ArrayList<>();
                channel.setCurrentUserIds(currentUsers);
            }
            
            // 检查用户是否已存在
            if (!currentUsers.contains(uid)) {
                currentUsers.add(uid);
                channel.setCurrentUserCount(currentUsers.size());
                
                // 更新最大用户数
                if (channel.getCurrentUserCount() > channel.getMaxUserCount()) {
                    channel.setMaxUserCount(channel.getCurrentUserCount());
                }
                
                // 更新总用户数
                channel.setTotalUserCount(channel.getTotalUserCount() + 1);
                
                // 更新状态和时间
                channel.setStatus("active");
                channel.setUpdateTime(System.currentTimeMillis());
                channel.setLastActiveTime(System.currentTimeMillis());
                
                log.info("用户添加成功 - uid: {}, 当前用户数: {}", uid, channel.getCurrentUserCount());
            } else {
                log.debug("用户已存在于频道中 - uid: {}", uid);
            }
            
            return channelRepository.save(channel);
        } else {
            throw new RuntimeException("频道不存在");
        }
    }
    
    @Override
    @Transactional
    public RtcChannelEntity removeUserFromChannel(String appId, String channelName, String uid) {
        log.info("从频道移除用户 - appId: {}, channelName: {}, uid: {}", appId, channelName, uid);
        
        Optional<RtcChannelEntity> channelOpt = findChannel(appId, channelName);
        if (channelOpt.isPresent()) {
            RtcChannelEntity channel = channelOpt.get();
            List<String> currentUsers = channel.getCurrentUserIds();
            if (currentUsers != null && currentUsers.remove(uid)) {
                channel.setCurrentUserCount(currentUsers.size());
                
                // 如果没有用户了，设置为非活跃状态
                if (currentUsers.isEmpty()) {
                    channel.setStatus("inactive");
                }
                
                channel.setUpdateTime(System.currentTimeMillis());
                channel.setLastActiveTime(System.currentTimeMillis());
                
                log.info("用户移除成功 - uid: {}, 当前用户数: {}", uid, channel.getCurrentUserCount());
            } else {
                log.debug("用户不存在于频道中 - uid: {}", uid);
            }
            
            return channelRepository.save(channel);
        } else {
            throw new RuntimeException("频道不存在");
        }
    }
    
    @Override
    @Transactional
    public RtcChannelEntity updateUserCount(String appId, String channelName, Integer userCount) {
        log.info("更新频道用户数量 - appId: {}, channelName: {}, userCount: {}", appId, channelName, userCount);
        
        Optional<RtcChannelEntity> channelOpt = findChannel(appId, channelName);
        if (channelOpt.isPresent()) {
            RtcChannelEntity channel = channelOpt.get();
            channel.setCurrentUserCount(userCount);
            
            // 更新最大用户数
            if (userCount > channel.getMaxUserCount()) {
                channel.setMaxUserCount(userCount);
            }
            
            // 根据用户数更新状态
            channel.setStatus(userCount > 0 ? "active" : "inactive");
            channel.setUpdateTime(System.currentTimeMillis());
            channel.setLastActiveTime(System.currentTimeMillis());
            
            return channelRepository.save(channel);
        } else {
            throw new RuntimeException("频道不存在");
        }
    }
    
    @Override
    @Transactional
    public int syncChannelsFromAPI(String appId, String basicAuth) {
        log.info("从API同步频道列表 - appId: {}", appId);
        
        try {
            // 调用声网API获取频道列表
            ChannelListResponse response = rtcChannelAPIService.getChannelList(appId, 0, 100, basicAuth);
            
            if (response != null && response.getData() != null && response.getData().getChannels() != null) {
                int syncCount = 0;
                
                for (ChannelInfo channelInfo : response.getData().getChannels()) {
                    String channelName = channelInfo.getChannelName();
                    Integer userNum = channelInfo.getUserCount();
                    
                    // 创建或更新频道记录
                    Optional<RtcChannelEntity> existingChannel = findChannel(appId, channelName);
                    if (existingChannel.isPresent()) {
                        // 更新现有频道
                        RtcChannelEntity channel = existingChannel.get();
                        channel.setCurrentUserCount(userNum);
                        channel.setStatus(userNum > 0 ? "active" : "inactive");
                        channel.setUpdateTime(System.currentTimeMillis());
                        channel.setLastActiveTime(System.currentTimeMillis());
                        channelRepository.save(channel);
                    } else {
                        // 创建新频道
                        createOrUpdateChannel(appId, channelName, null, null, "从API同步");
                    }
                    syncCount++;
                }
                
                log.info("频道同步完成 - appId: {}, 同步数量: {}", appId, syncCount);
                return syncCount;
            }
        } catch (Exception e) {
            log.error("频道同步失败 - appId: {}", appId, e);
        }
        
        return 0;
    }
    
    @Override
    public RtcChannelEntity syncChannelFromAPI(String appId, String channelName, String basicAuth) {
        log.info("从API同步单个频道 - appId: {}, channelName: {}", appId, channelName);
        
        try {
            // 获取频道用户列表来确认频道存在
            UserListResponseDto userList = rtcChannelAPIService.getUserList(appId, channelName, basicAuth);
            
            if (userList != null && userList.getData() != null) {
                boolean channelExists = userList.getData().getChannelExist() != null && userList.getData().getChannelExist();
                int userCount = channelExists ? 
                    (userList.getData().getUsers() != null ? userList.getData().getUsers().size() : 0) : 0;
                
                // 创建或更新频道
                RtcChannelEntity channel = createOrUpdateChannel(appId, channelName, null, null, "从API同步");
                channel.setCurrentUserCount(userCount);
                channel.setStatus(userCount > 0 ? "active" : "inactive");
                
                return channelRepository.save(channel);
            }
        } catch (Exception e) {
            log.error("频道同步失败 - appId: {}, channelName: {}", appId, channelName, e);
        }
        
        return null;
    }
    
    @Override
    public RtcChannelEntity syncChannelUsersFromAPI(String appId, String channelName, String basicAuth) {
        log.info("从API同步频道用户列表 - appId: {}, channelName: {}", appId, channelName);
        
        try {
            UserListResponseDto userList = rtcChannelAPIService.getUserList(appId, channelName, basicAuth);
            
            if (userList != null && userList.getData() != null) {
                boolean channelExists = userList.getData().getChannelExist() != null && userList.getData().getChannelExist();
                if (channelExists) {
                    Optional<RtcChannelEntity> channelOpt = findChannel(appId, channelName);
                    if (channelOpt.isPresent()) {
                        RtcChannelEntity channel = channelOpt.get();
                        
                        // 更新用户列表
                        List<String> userIds = userList.getData().getUids();
                        if (userIds != null) {
                            channel.setCurrentUserIds(new ArrayList<>(userIds));
                            channel.setCurrentUserCount(userIds.size());
                        } else {
                            channel.setCurrentUserIds(new ArrayList<>());
                            channel.setCurrentUserCount(0);
                        }
                        
                        channel.setStatus(channel.getCurrentUserCount() > 0 ? "active" : "inactive");
                        channel.setUpdateTime(System.currentTimeMillis());
                        channel.setLastActiveTime(System.currentTimeMillis());
                        
                        return channelRepository.save(channel);
                    }
                } else {
                    log.warn("频道不存在于声网API中 - appId: {}, channelName: {}", appId, channelName);
                }
            }
        } catch (Exception e) {
            log.error("频道用户同步失败 - appId: {}, channelName: {}", appId, channelName, e);
        }
        
        return null;
    }
    
    @Override
    public Map<String, Object> getChannelStatistics(String appId) {
        log.debug("获取频道统计信息 - appId: {}", appId);
        
        Map<String, Object> stats = new HashMap<>();
        
        // 基础统计
        stats.put("totalChannels", channelRepository.countByAppId(appId));
        stats.put("activeChannels", channelRepository.countByAppIdAndStatus(appId, "active"));
        stats.put("inactiveChannels", channelRepository.countByAppIdAndStatus(appId, "inactive"));
        
        // 获取活跃频道列表计算用户统计
        List<RtcChannelEntity> activeChannels = getActiveChannels(appId);
        int totalActiveUsers = activeChannels.stream()
            .mapToInt(channel -> channel.getCurrentUserCount() != null ? channel.getCurrentUserCount() : 0)
            .sum();
        
        stats.put("totalActiveUsers", totalActiveUsers);
        stats.put("averageUsersPerChannel", 
            activeChannels.size() > 0 ? (double) totalActiveUsers / activeChannels.size() : 0.0);
        
        // 最大用户数统计
        OptionalInt maxUsers = activeChannels.stream()
            .mapToInt(channel -> channel.getMaxUserCount() != null ? channel.getMaxUserCount() : 0)
            .max();
        stats.put("maxUsersInAnyChannel", maxUsers.orElse(0));
        
        log.debug("频道统计信息: {}", stats);
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getChannelUsageTrend(String appId, int days) {
        log.debug("获取频道使用趋势 - appId: {}, days: {}", appId, days);
        
        // 简化实现：按天统计频道数量
        List<Map<String, Object>> trend = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        long oneDayMillis = 24 * 60 * 60 * 1000L;
        
        for (int i = days - 1; i >= 0; i--) {
            long dayStart = currentTime - (i + 1) * oneDayMillis;
            long dayEnd = currentTime - i * oneDayMillis;
            
            // 查找在该时间段内活跃的频道
            List<RtcChannelEntity> channels = channelRepository.findByAppId(appId, Pageable.unpaged()).getContent();
            long activeChannels = channels.stream()
                .filter(channel -> channel.getLastActiveTime() != null && 
                        channel.getLastActiveTime() >= dayStart && 
                        channel.getLastActiveTime() < dayEnd)
                .count();
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", new Date(dayEnd - oneDayMillis));
            dayData.put("activeChannels", activeChannels);
            trend.add(dayData);
        }
        
        return trend;
    }
    
    @Override
    public List<RtcChannelEntity> getPopularChannels(String appId, int limit) {
        log.debug("获取热门频道 - appId: {}, limit: {}", appId, limit);
        
        Pageable pageable = PageRequest.of(0, limit, 
            Sort.by(Sort.Direction.DESC, "maxUserCount", "currentUserCount"));
        
        return channelRepository.findByAppId(appId, pageable).getContent();
    }
    
    @Override
    @Transactional
    public int cleanupInactiveChannels(int inactiveThresholdHours) {
        log.info("清理非活跃频道 - 阈值: {} 小时", inactiveThresholdHours);
        
        long thresholdTime = System.currentTimeMillis() - (inactiveThresholdHours * 60 * 60 * 1000L);
        List<RtcChannelEntity> inactiveChannels = channelRepository.findChannelsForCleanup(thresholdTime, "inactive");
        
        int cleanupCount = inactiveChannels.size();
        if (cleanupCount > 0) {
            channelRepository.deleteAll(inactiveChannels);
            log.info("清理非活跃频道完成 - 清理数量: {}", cleanupCount);
        }
        
        return cleanupCount;
    }
    
    @Override
    @Transactional
    public int cleanupExpiredChannels(int retentionDays) {
        log.info("清理过期频道数据 - 保留天数: {}", retentionDays);
        
        long thresholdTime = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L);
        List<RtcChannelEntity> expiredChannels = channelRepository.findByCreateTimeLessThan(thresholdTime);
        
        int cleanupCount = expiredChannels.size();
        if (cleanupCount > 0) {
            channelRepository.deleteByCreateTimeLessThan(thresholdTime);
            log.info("清理过期频道完成 - 清理数量: {}", cleanupCount);
        }
        
        return cleanupCount;
    }
    
    @Override
    @Transactional
    public int repairChannelDataConsistency(String appId) {
        log.info("修复频道数据一致性 - appId: {}", appId);
        
        List<RtcChannelEntity> channels = channelRepository.findByAppId(appId, Pageable.unpaged()).getContent();
        int repairCount = 0;
        
        for (RtcChannelEntity channel : channels) {
            boolean needsRepair = false;
            
            // 修复用户数量一致性
            if (channel.getCurrentUserIds() != null) {
                Integer actualUserCount = channel.getCurrentUserIds().size();
                if (!actualUserCount.equals(channel.getCurrentUserCount())) {
                    channel.setCurrentUserCount(actualUserCount);
                    needsRepair = true;
                }
            }
            
            // 修复状态一致性
            if (channel.getCurrentUserCount() != null) {
                String expectedStatus = channel.getCurrentUserCount() > 0 ? "active" : "inactive";
                if (!expectedStatus.equals(channel.getStatus())) {
                    channel.setStatus(expectedStatus);
                    needsRepair = true;
                }
            }
            
            // 确保时间字段不为空
            if (channel.getCreateTime() == null) {
                channel.setCreateTime(System.currentTimeMillis());
                needsRepair = true;
            }
            if (channel.getUpdateTime() == null) {
                channel.setUpdateTime(System.currentTimeMillis());
                needsRepair = true;
            }
            if (channel.getLastActiveTime() == null) {
                channel.setLastActiveTime(channel.getUpdateTime());
                needsRepair = true;
            }
            
            // 初始化缺失的字段
            if (channel.getMaxUserCount() == null) {
                channel.setMaxUserCount(channel.getCurrentUserCount() != null ? channel.getCurrentUserCount() : 0);
                needsRepair = true;
            }
            if (channel.getTotalUserCount() == null) {
                channel.setTotalUserCount(0);
                needsRepair = true;
            }
            if (channel.getCurrentUserIds() == null) {
                channel.setCurrentUserIds(new ArrayList<>());
                needsRepair = true;
            }
            if (channel.getProperties() == null) {
                channel.setProperties(new HashMap<>());
                needsRepair = true;
            }
            
            if (needsRepair) {
                channelRepository.save(channel);
                repairCount++;
            }
        }
        
        log.info("频道数据一致性修复完成 - appId: {}, 修复数量: {}", appId, repairCount);
        return repairCount;
    }
    
    // ==================== 封禁规则管理 ====================
    
    /**
     * 创建封禁规则并同步到声网API
     * 
     * @param appId 应用ID
     * @param channelName 频道名称（可选）
     * @param uid 用户ID（可选）
     * @param ip IP地址（可选）
     * @param privileges 封禁权限列表
     * @param timeInSeconds 封禁时长（秒）
     * @param basicAuth Basic认证字符串
     * @return 封禁规则响应
     */
    @Transactional
    public BanRuleResponse createBanRuleWithSync(String appId, String channelName, Long uid,
                                                 String ip, List<String> privileges,
                                                 Integer timeInSeconds, String basicAuth) {
        log.info("创建封禁规则 - appId: {}, channelName: {}, uid: {}, privileges: {}", 
                appId, channelName, uid, privileges);
        
        try {
            BanRuleCreateRequest request = new BanRuleCreateRequest();
            request.setAppid(appId);
            request.setCname(channelName);
            request.setUid(uid);
            request.setIp(ip);
            request.setTimeInSeconds(timeInSeconds);
            request.setPrivileges(privileges);
            
            BanRuleResponse response = rtcChannelAPIService.createBanRule(request, basicAuth);
            log.info("封禁规则创建成功 - ruleId: {}", response.getId());
            return response;
        } catch (Exception e) {
            log.error("创建封禁规则失败 - appId: {}, channelName: {}", appId, channelName, e);
            throw new RuntimeException("创建封禁规则失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取封禁规则列表
     * 
     * @param appId 应用ID
     * @param basicAuth Basic认证字符串
     * @return 封禁规则列表响应
     */
    public BanRuleListResponse getBanRuleListFromAPI(String appId, String basicAuth) {
        log.info("获取封禁规则列表 - appId: {}", appId);
        
        try {
            BanRuleListResponse response = rtcChannelAPIService.getBanRuleList(appId, basicAuth);
            log.info("获取封禁规则列表成功 - 规则数量: {}", 
                    response.getRules() != null ? response.getRules().size() : 0);
            return response;
        } catch (Exception e) {
            log.error("获取封禁规则列表失败 - appId: {}", appId, e);
            throw new RuntimeException("获取封禁规则列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新封禁规则
     * 
     * @param ruleId 规则ID
     * @param appId 应用ID
     * @param channelName 频道名称（可选）
     * @param uid 用户ID（可选）
     * @param ip IP地址（可选）
     * @param privileges 封禁权限列表
     * @param timeInSeconds 封禁时长（秒）
     * @param basicAuth Basic认证字符串
     * @return 封禁规则响应
     */
    @Transactional
    public BanRuleResponseDto updateBanRuleWithSync(Long ruleId, String appId, String channelName,
                                                    Long uid, String ip, List<String> privileges,
                                                    Integer timeInSeconds, String basicAuth) {
        log.info("更新封禁规则 - ruleId: {}, appId: {}, channelName: {}", ruleId, appId, channelName);
        
        try {
            BanRuleUpdateRequest request = new BanRuleUpdateRequest();
            request.setAppid(appId);
            request.setCname(channelName);
            request.setUid(uid);
            request.setIp(ip);
            request.setTimeInSeconds(timeInSeconds);
            request.setPrivileges(privileges);

            BanRuleResponseDto response = rtcChannelAPIService.updateBanRule(ruleId, request, basicAuth);
            log.info("封禁规则更新成功 - ruleId: {}", ruleId);
            return response;
        } catch (Exception e) {
            log.error("更新封禁规则失败 - ruleId: {}", ruleId, e);
            throw new RuntimeException("更新封禁规则失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除封禁规则
     * 
     * @param appId 应用ID
     * @param ruleId 规则ID
     * @param basicAuth Basic认证字符串
     * @return 封禁规则响应
     */
    @Transactional
    public BanRuleResponse deleteBanRuleWithSync(String appId, Long ruleId, String basicAuth) {
        log.info("删除封禁规则 - appId: {}, ruleId: {}", appId, ruleId);
        
        try {
            BanRuleResponse response = rtcChannelAPIService.deleteBanRule(appId, ruleId, basicAuth);
            log.info("封禁规则删除成功 - ruleId: {}", ruleId);
            return response;
        } catch (Exception e) {
            log.error("删除封禁规则失败 - ruleId: {}", ruleId, e);
            throw new RuntimeException("删除封禁规则失败: " + e.getMessage(), e);
        }
    }

    // ==================== 增强的频道用户管理 ====================
    
    /**
     * 添加用户到频道并验证API状态
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param uid 用户ID
     * @param basicAuth Basic认证字符串（可选，用于API验证）
     * @return 更新后的频道信息
     */
    @Override
    @Transactional
    public RtcChannelEntity addUserToChannelWithSync(String appId, String channelName, String uid, String basicAuth) {
        log.info("添加用户到频道并同步验证 - appId: {}, channelName: {}, uid: {}", appId, channelName, uid);
        
        // 先添加到本地数据库
        RtcChannelEntity channel = addUserToChannel(appId, channelName, uid);
        
        // 如果提供了basicAuth，则验证API中的实际状态
        if (basicAuth != null && !basicAuth.trim().isEmpty()) {
            try {
                UserListResponseDto userList = rtcChannelAPIService.getUserList(appId, channelName, basicAuth);
                if (userList != null && userList.getData() != null) {
                    Boolean channelExists = userList.getData().getChannelExist();
                    if (Boolean.TRUE.equals(channelExists)) {
                        // 频道在API中存在，同步用户列表
                        List<String> apiUserIds = userList.getData().getUids();
                        if (apiUserIds != null) {
                            // 更新本地用户列表以匹配API
                            channel.setCurrentUserIds(new ArrayList<>(apiUserIds));
                            channel.setCurrentUserCount(apiUserIds.size());
                            channel = channelRepository.save(channel);
                            log.info("同步API用户列表成功 - 用户数量: {}", apiUserIds.size());
                        }
                    } else {
                        log.warn("频道在API中不存在，仅保持本地记录 - appId: {}, channelName: {}", appId, channelName);
                    }
                }
            } catch (Exception e) {
                log.warn("验证API状态失败，仅保持本地记录 - appId: {}, channelName: {}, error: {}", 
                        appId, channelName, e.getMessage());
            }
        }
        
        return channel;
    }
    
    /**
     * 从频道移除用户并验证API状态
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @param uid 用户ID
     * @param basicAuth Basic认证字符串（可选，用于API验证）
     * @return 更新后的频道信息
     */
    @Transactional
    public RtcChannelEntity removeUserFromChannelWithSync(String appId, String channelName, String uid, String basicAuth) {
        log.info("从频道移除用户并同步验证 - appId: {}, channelName: {}, uid: {}", appId, channelName, uid);
        
        // 先从本地数据库移除
        RtcChannelEntity channel = removeUserFromChannel(appId, channelName, uid);
        
        // 如果提供了basicAuth，则验证API中的实际状态
        if (basicAuth != null && !basicAuth.trim().isEmpty()) {
            try {
                UserListResponseDto userList = rtcChannelAPIService.getUserList(appId, channelName, basicAuth);
                if (userList != null && userList.getData() != null) {
                    Boolean channelExists = userList.getData().getChannelExist();
                    if (Boolean.TRUE.equals(channelExists)) {
                        // 频道在API中存在，同步用户列表
                        List<String> apiUserIds = userList.getData().getUids();
                        if (apiUserIds != null) {
                            // 更新本地用户列表以匹配API
                            channel.setCurrentUserIds(new ArrayList<>(apiUserIds));
                            channel.setCurrentUserCount(apiUserIds.size());
                            channel.setStatus(apiUserIds.isEmpty() ? "inactive" : "active");
                            channel = channelRepository.save(channel);
                            log.info("同步API用户列表成功 - 用户数量: {}", apiUserIds.size());
                        }
                    } else {
                        log.info("频道在API中不存在，设置为非活跃状态 - appId: {}, channelName: {}", appId, channelName);
                        channel.setStatus("inactive");
                        channel.setCurrentUserCount(0);
                        channel.setCurrentUserIds(new ArrayList<>());
                        channel = channelRepository.save(channel);
                    }
                }
            } catch (Exception e) {
                log.warn("验证API状态失败，仅保持本地记录 - appId: {}, channelName: {}, error: {}", 
                        appId, channelName, e.getMessage());
            }
        }
        
        return channel;
    }
    
    /**
     * 生成频道ID
     * 
     * @param appId 应用ID
     * @param channelName 频道名称
     * @return 频道ID
     */
    private String generateChannelId(String appId, String channelName) {
        return appId + "_" + channelName;
    }
}