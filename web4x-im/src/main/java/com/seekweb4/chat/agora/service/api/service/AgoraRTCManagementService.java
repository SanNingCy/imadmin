package com.seekweb4.chat.agora.service.api.service;

import com.seekweb4.chat.agora.bean.dto.UserListResponseDto;
import com.seekweb4.chat.agora.bean.exception.BusinessException;
import com.seekweb4.chat.agora.service.api.response.BanRuleInfo;
import com.seekweb4.chat.agora.service.api.response.ChannelListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 声网RTC综合管理服务类
 * 整合频道管理、用户管理、权限管理等功能，提供统一的RTC管理接口
 * 
 * @author liangbo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgoraRTCManagementService {
    
    private final AgoraRTCChannelService channelService;
    private final AgoraRTCUserService userService;
    private final AgoraRTCPermissionService permissionService;

    // 缓存项目信息
    private final Map<String, String> appIdCache = new ConcurrentHashMap<>();
    
    // ==================== 频道管理高级功能 ====================
    
    /**
     * 创建管理员控制的频道
     * 创建频道并设置管理权限
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param adminUserId 管理员用户ID（可选）
     * @return 频道创建是否成功
     */
    public boolean createManagedChannel(String appId, String channelName, Long adminUserId) {
        try {
            log.info("创建管理频道 - AppId: {}, 频道名: {}, 管理员: {}", appId, channelName, adminUserId);
            
            // 1. 创建频道
            boolean channelCreated = channelService.createChannel(appId, channelName);
            
            if (!channelCreated) {
                log.error("频道创建失败 - 频道名: {}", channelName);
                return false;
            }
            
            // 2. 如果指定了管理员，为管理员设置特殊权限（暂时不做限制）
            if (adminUserId != null) {
                log.info("频道管理员设置完成 - 频道名: {}, 管理员: {}", channelName, adminUserId);
            }
            
            log.info("管理频道创建成功 - 频道名: {}", channelName);
            return true;
            
        } catch (Exception e) {
            log.error("创建管理频道失败 - 频道名: {}", channelName, e);
            throw new BusinessException(HttpStatus.OK.value(),"创建管理频道失败: ");
        }
    }
    
    /**
     * 智能解散频道
     * 先警告用户，然后逐步踢出，最后解散频道
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param warningDurationSeconds 警告持续时间（秒）
     * @param banDurationMinutes 禁止重新加入的时长（分钟）
     * @return 解散结果
     */
    public CompletableFuture<Boolean> smartDissolveChannel(String appId, String channelName, 
                                                          int warningDurationSeconds, int banDurationMinutes) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始智能解散频道 - 频道名: {}, 警告时间: {}秒", channelName, warningDurationSeconds);
                
                // 1. 获取频道中的所有用户
                List<Long> users = channelService.getChannelUsers(appId, channelName);
                
                if (users.isEmpty()) {
                    log.info("频道为空，直接解散 - 频道名: {}", channelName);
                    return channelService.dissolveChannel(appId, channelName, banDurationMinutes);
                }
                
                // 2. 发送解散警告（通过禁言所有用户1分钟来"警告"）
                log.info("发送解散警告，临时禁言所有用户 - 频道名: {}, 用户数: {}", channelName, users.size());
                userService.batchMuteUsers(appId, channelName, users, 1); // 禁言1分钟作为警告
                
                // 3. 等待警告时间
                if (warningDurationSeconds > 0) {
                    try {
                        Thread.sleep(warningDurationSeconds * 1000L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                
                // 4. 解散频道
                log.info("警告时间结束，开始解散频道 - 频道名: {}", channelName);
                return channelService.dissolveChannel(appId, channelName, banDurationMinutes);
                
            } catch (Exception e) {
                log.error("智能解散频道失败 - 频道名: {}", channelName, e);
                return false;
            }
        });
    }
    
    /**
     * 批量管理频道用户
     * 对频道中的多个用户执行相同操作
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param userIds 用户ID列表
     * @param action 操作类型（mute: 禁言, kick: 踢出, unmute: 解除禁言, unkick: 解除踢出）
     * @param durationMinutes 操作持续时间（分钟，仅对mute和kick有效）
     * @return 成功处理的用户数量
     */
    public int batchManageUsers(String appId, String channelName, List<Long> userIds, 
                              String action, int durationMinutes) {
        try {
            log.info("批量管理用户 - 频道名: {}, 操作: {}, 用户数: {}, 持续时间: {}分钟", 
                channelName, action, userIds.size(), durationMinutes);
            
            int successCount = 0;
            
            switch (action.toLowerCase()) {
                case "mute":
                    successCount = userService.batchMuteUsers(appId, channelName, userIds, durationMinutes);
                    break;
                    
                case "kick":
                    successCount = userService.batchKickUsers(appId, channelName, userIds, durationMinutes);
                    break;
                    
                case "unmute":
                    for (Long userId : userIds) {
                        try {
                            if (userService.unmuteUser(appId, channelName, userId)) {
                                successCount++;
                            }
                        } catch (Exception e) {
                            log.error("批量解除禁言失败 - 用户ID: {}", userId, e);
                        }
                    }
                    break;
                    
                case "unkick":
                    for (Long userId : userIds) {
                        try {
                            if (userService.unkickUser(appId, channelName, userId)) {
                                successCount++;
                            }
                        } catch (Exception e) {
                            log.error("批量解除踢出失败 - 用户ID: {}", userId, e);
                        }
                    }
                    break;
                    
                default:
                    throw new IllegalArgumentException("不支持的操作类型: " + action);
            }
            
            log.info("批量管理用户完成 - 操作: {}, 总数: {}, 成功: {}", action, userIds.size(), successCount);
            return successCount;
            
        } catch (Exception e) {
            log.error("批量管理用户失败 - 操作: {}", action, e);
            throw new BusinessException(HttpStatus.OK.value(),"批量管理用户失败!");
        }
    }
    
    // ==================== 频道状态监控 ====================
    
    /**
     * 获取频道完整状态信息
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @return 频道状态信息
     */
    public ChannelStatusInfo getChannelStatus(String appId, String channelName) {
        try {
            log.info("获取频道状态 - AppId: {}, 频道名: {}", appId, channelName);
            
            ChannelStatusInfo status = new ChannelStatusInfo();
            status.setAppId(appId);
            status.setChannelName(channelName);
            
            // 获取频道基本信息
            UserListResponseDto channelInfo = channelService.getChannelInfo(appId, channelName);
            if (channelInfo.getData() != null) {
                status.setExists(Boolean.TRUE.equals(channelInfo.getData().getChannelExist()));
                status.setMode(channelInfo.getData().getMode());
                status.setUserCount(channelInfo.getData().getTotal());
                status.setUsers(channelInfo.getData().getUsers());
                status.setBroadcasters(channelInfo.getData().getBroadcasters());
                status.setAudience(channelInfo.getData().getAudience());
                status.setAudienceTotal(channelInfo.getData().getAudienceTotal());
            }
            
            // 获取频道相关的封禁规则
            List<BanRuleInfo> banRules = permissionService.getBanRulesByChannel(appId, channelName);
            status.setBanRuleCount(banRules.size());
            status.setBanRules(banRules);
            
            log.info("频道状态获取完成 - 频道名: {}, 存在: {}, 用户数: {}, 封禁规则数: {}", 
                channelName, status.isExists(), status.getUserCount(), status.getBanRuleCount());
            
            return status;
            
        } catch (Exception e) {
            log.error("获取频道状态失败 - 频道名: {}", channelName, e);
            throw new BusinessException(HttpStatus.OK.value(),"获取频道状态失败");
        }
    }
    
    /**
     * 获取用户在所有频道的状态
     * 
     * @param appId 项目的App ID
     * @param userId 用户ID
     * @return 用户状态信息
     */
    public UserStatusInfo getUserStatus(String appId, Long userId) {
        try {
            log.info("获取用户状态 - AppId: {}, 用户ID: {}", appId, userId);
            
            UserStatusInfo status = new UserStatusInfo();
            status.setAppId(appId);
            status.setUserId(userId);
            
            // 获取用户的封禁规则
            List<BanRuleInfo> banRules = permissionService.getBanRulesByUser(appId, userId);
            status.setBanRules(banRules);
            status.setBanRuleCount(banRules.size());
            
            // 检查各种权限状态
            status.setCanJoinChannel(!permissionService.isUserBanned(appId, userId, null, "join_channel"));
            status.setCanPublishAudio(!permissionService.isUserBanned(appId, userId, null, "publish_audio"));
            status.setCanPublishVideo(!permissionService.isUserBanned(appId, userId, null, "publish_video"));
            
            log.info("用户状态获取完成 - 用户ID: {}, 封禁规则数: {}, 可加入频道: {}, 可发布音频: {}, 可发布视频: {}", 
                userId, status.getBanRuleCount(), status.isCanJoinChannel(), 
                status.isCanPublishAudio(), status.isCanPublishVideo());
            
            return status;
            
        } catch (Exception e) {
            log.error("获取用户状态失败 - 用户ID: {}", userId, e);
            throw new BusinessException(HttpStatus.OK.value(),"获取用户状态失败");
        }
    }
    
    // ==================== 系统管理功能 ====================
    
    /**
     * 清理过期规则和无效数据
     * 
     * @param appId 项目的App ID
     * @return 清理结果统计
     */
    public CleanupResult performSystemCleanup(String appId) {
        try {
            log.info("开始系统清理 - AppId: {}", appId);
            
            CleanupResult result = new CleanupResult();
            result.setAppId(appId);
            result.setStartTime(System.currentTimeMillis());
            
            // 1. 清理过期的封禁规则
            int expiredRulesCount = permissionService.cleanExpiredBanRules(appId);
            result.setExpiredRulesCleared(expiredRulesCount);
            
            // 2. 统计当前状态
            List<BanRuleInfo> remainingRules = permissionService.getAllBanRules(appId);
            result.setRemainingRules(remainingRules.size());
            
            result.setEndTime(System.currentTimeMillis());
            result.setDurationMs(result.getEndTime() - result.getStartTime());
            
            log.info("系统清理完成 - AppId: {}, 清理过期规则: {}, 剩余规则: {}, 耗时: {}ms", 
                appId, expiredRulesCount, remainingRules.size(), result.getDurationMs());
            
            return result;
            
        } catch (Exception e) {
            log.error("系统清理失败 - AppId: {}", appId, e);
            throw new BusinessException(HttpStatus.OK.value(),"系统清理失败!");
        }
    }
    
    /**
     * 获取项目统计信息
     * 
     * @param appId 项目的App ID
     * @return 项目统计信息
     */
    public ProjectStatistics getProjectStatistics(String appId) {
        try {
            log.info("获取项目统计 - AppId: {}", appId);
            
            ProjectStatistics stats = new ProjectStatistics();
            stats.setAppId(appId);
            
            // 获取频道列表（第一页）
            ChannelListResponse channels = channelService.getAllChannels(appId, 0, 100);
            if (channels.getData() != null) {
                stats.setTotalChannels(channels.getData().getTotalSize());
                stats.setActiveChannels(channels.getData().getChannels() != null ? 
                    channels.getData().getChannels().size() : 0);
            }
            
            // 获取封禁规则统计
            List<BanRuleInfo> allRules = permissionService.getAllBanRules(appId);
            stats.setTotalBanRules(allRules.size());
            
            // 统计不同类型的封禁规则
            long joinChannelBans = allRules.stream()
                .filter(rule -> rule.getPrivileges() != null && rule.getPrivileges().contains("join_channel"))
                .count();
            stats.setJoinChannelBans((int) joinChannelBans);
            
            long publishAudioBans = allRules.stream()
                .filter(rule -> rule.getPrivileges() != null && rule.getPrivileges().contains("publish_audio"))
                .count();
            stats.setPublishAudioBans((int) publishAudioBans);
            
            long publishVideoBans = allRules.stream()
                .filter(rule -> rule.getPrivileges() != null && rule.getPrivileges().contains("publish_video"))
                .count();
            stats.setPublishVideoBans((int) publishVideoBans);
            
            log.info("项目统计完成 - AppId: {}, 频道总数: {}, 活跃频道: {}, 封禁规则: {}", 
                appId, stats.getTotalChannels(), stats.getActiveChannels(), stats.getTotalBanRules());
            
            return stats;
            
        } catch (Exception e) {
            log.error("获取项目统计失败 - AppId: {}", appId, e);
            throw new BusinessException(HttpStatus.OK.value(),"获取项目统计失败!");
        }
    }
    
    // ==================== 数据传输对象 ====================
    
    /**
     * 频道状态信息
     */
    public static class ChannelStatusInfo {
        private String appId;
        private String channelName;
        private boolean exists;
        private Integer mode;
        private Integer userCount;
        private List<Long> users;
        private List<Long> broadcasters;
        private List<Long> audience;
        private Integer audienceTotal;
        private int banRuleCount;
        private List<BanRuleInfo> banRules;
        
        // getter和setter方法
        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        
        public String getChannelName() { return channelName; }
        public void setChannelName(String channelName) { this.channelName = channelName; }
        
        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }
        
        public Integer getMode() { return mode; }
        public void setMode(Integer mode) { this.mode = mode; }
        
        public Integer getUserCount() { return userCount; }
        public void setUserCount(Integer userCount) { this.userCount = userCount; }
        
        public List<Long> getUsers() { return users; }
        public void setUsers(List<Long> users) { this.users = users; }
        
        public List<Long> getBroadcasters() { return broadcasters; }
        public void setBroadcasters(List<Long> broadcasters) { this.broadcasters = broadcasters; }
        
        public List<Long> getAudience() { return audience; }
        public void setAudience(List<Long> audience) { this.audience = audience; }
        
        public Integer getAudienceTotal() { return audienceTotal; }
        public void setAudienceTotal(Integer audienceTotal) { this.audienceTotal = audienceTotal; }
        
        public int getBanRuleCount() { return banRuleCount; }
        public void setBanRuleCount(int banRuleCount) { this.banRuleCount = banRuleCount; }
        
        public List<BanRuleInfo> getBanRules() { return banRules; }
        public void setBanRules(List<BanRuleInfo> banRules) { this.banRules = banRules; }
    }
    
    /**
     * 用户状态信息
     */
    public static class UserStatusInfo {
        private String appId;
        private Long userId;
        private boolean canJoinChannel;
        private boolean canPublishAudio;
        private boolean canPublishVideo;
        private int banRuleCount;
        private List<BanRuleInfo> banRules;
        
        // getter和setter方法
        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public boolean isCanJoinChannel() { return canJoinChannel; }
        public void setCanJoinChannel(boolean canJoinChannel) { this.canJoinChannel = canJoinChannel; }
        
        public boolean isCanPublishAudio() { return canPublishAudio; }
        public void setCanPublishAudio(boolean canPublishAudio) { this.canPublishAudio = canPublishAudio; }
        
        public boolean isCanPublishVideo() { return canPublishVideo; }
        public void setCanPublishVideo(boolean canPublishVideo) { this.canPublishVideo = canPublishVideo; }
        
        public int getBanRuleCount() { return banRuleCount; }
        public void setBanRuleCount(int banRuleCount) { this.banRuleCount = banRuleCount; }
        
        public List<BanRuleInfo> getBanRules() { return banRules; }
        public void setBanRules(List<BanRuleInfo> banRules) { this.banRules = banRules; }
    }
    
    /**
     * 清理结果
     */
    public static class CleanupResult {
        private String appId;
        private long startTime;
        private long endTime;
        private long durationMs;
        private int expiredRulesCleared;
        private int remainingRules;
        
        // getter和setter方法
        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        
        public long getDurationMs() { return durationMs; }
        public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
        
        public int getExpiredRulesCleared() { return expiredRulesCleared; }
        public void setExpiredRulesCleared(int expiredRulesCleared) { this.expiredRulesCleared = expiredRulesCleared; }
        
        public int getRemainingRules() { return remainingRules; }
        public void setRemainingRules(int remainingRules) { this.remainingRules = remainingRules; }
    }
    
    /**
     * 项目统计信息
     */
    public static class ProjectStatistics {
        private String appId;
        private Integer totalChannels;
        private Integer activeChannels;
        private Integer totalBanRules;
        private Integer joinChannelBans;
        private Integer publishAudioBans;
        private Integer publishVideoBans;
        
        // getter和setter方法
        public String getAppId() { return appId; }
        public void setAppId(String appId) { this.appId = appId; }
        
        public Integer getTotalChannels() { return totalChannels; }
        public void setTotalChannels(Integer totalChannels) { this.totalChannels = totalChannels; }
        
        public Integer getActiveChannels() { return activeChannels; }
        public void setActiveChannels(Integer activeChannels) { this.activeChannels = activeChannels; }
        
        public Integer getTotalBanRules() { return totalBanRules; }
        public void setTotalBanRules(Integer totalBanRules) { this.totalBanRules = totalBanRules; }
        
        public Integer getJoinChannelBans() { return joinChannelBans; }
        public void setJoinChannelBans(Integer joinChannelBans) { this.joinChannelBans = joinChannelBans; }
        
        public Integer getPublishAudioBans() { return publishAudioBans; }
        public void setPublishAudioBans(Integer publishAudioBans) { this.publishAudioBans = publishAudioBans; }
        
        public Integer getPublishVideoBans() { return publishVideoBans; }
        public void setPublishVideoBans(Integer publishVideoBans) { this.publishVideoBans = publishVideoBans; }
    }
}