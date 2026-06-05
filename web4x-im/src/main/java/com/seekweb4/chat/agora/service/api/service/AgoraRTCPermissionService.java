package com.seekweb4.chat.agora.service.api.service;

import com.seekweb4.chat.agora.bean.dto.BanRuleResponseDto;
import com.seekweb4.chat.agora.bean.exception.BusinessException;
import com.seekweb4.chat.agora.service.api.RtcChannelAPIService;
import com.seekweb4.chat.agora.service.api.request.BanRuleCreateRequest;
import com.seekweb4.chat.agora.service.api.request.BanRuleUpdateRequest;
import com.seekweb4.chat.agora.service.api.response.BanRuleInfo;
import com.seekweb4.chat.agora.service.api.response.BanRuleListResponse;
import com.seekweb4.chat.agora.service.api.response.BanRuleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 声网RTC权限管理服务类
 * 提供封禁规则的高级管理功能，包括规则的创建、查询、更新、删除等操作
 * 
 * @author liangbo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgoraRTCPermissionService {
    
    private final RtcChannelAPIService rtcChannelAPIService;

    @Value("${whitelist.rtcKickOutAuth.basicAuth}")
    private String basicAuth;

    /**
     * 创建封禁规则
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称（可选）
     * @param userId 用户ID（可选）
     * @param userIp 用户IP（可选）
     * @param privileges 要封禁的权限列表
     * @param banDurationMinutes 封禁时长（分钟）
     * @return 封禁规则ID
     */
    public Long createBanRule(String appId, String channelName, Long userId, String userIp, 
                             List<String> privileges, int banDurationMinutes) {
        try {
            log.info("创建封禁规则 - AppId: {}, 频道: {}, 用户ID: {}, IP: {}, 权限: {}, 时长: {}分钟", 
                appId, channelName, userId, userIp, privileges, banDurationMinutes);
            
            // 验证参数
            if (channelName == null && userId == null && userIp == null) {
                throw new IllegalArgumentException("频道名、用户ID、用户IP至少需要指定一个");
            }
            
            BanRuleCreateRequest request = new BanRuleCreateRequest();
            request.setAppid(appId);
            request.setCname(channelName);
            request.setUid(userId);
            request.setIp(userIp);
            request.setPrivileges(privileges);
            request.setTime(banDurationMinutes);
            
            BanRuleResponse response = rtcChannelAPIService.createBanRule(request,basicAuth);
            
            log.info("封禁规则创建成功 - 规则ID: {}", response.getId());
            return response.getId();
            
        } catch (Exception e) {
            log.error("创建封禁规则失败", e);
            throw new BusinessException("创建封禁规则失败!");
        }
    }
    
    /**
     * 更新封禁规则
     * 
     * @param appId 项目的App ID
     * @param ruleId 规则ID
     * @param channelName 频道名称
     * @param userId 用户ID
     * @param userIp 用户IP
     * @param privileges 要封禁的权限列表
     * @param banDurationMinutes 封禁时长（分钟）
     * @return 是否更新成功
     */
    public boolean updateBanRule(String appId, Long ruleId, String channelName, Long userId, String userIp,
                               List<String> privileges, int banDurationMinutes) {
        try {
            log.info("更新封禁规则 - 规则ID: {}, AppId: {}", ruleId, appId);
            
            BanRuleUpdateRequest request = new BanRuleUpdateRequest();
            request.setAppid(appId);
            request.setCname(channelName);
            request.setUid(userId);
            request.setIp(userIp);
            request.setPrivileges(privileges);
            request.setTime(banDurationMinutes);

            BanRuleResponseDto response = rtcChannelAPIService.updateBanRule(ruleId, request,basicAuth);
            
            boolean success = "success".equals(response.getStatus());
            log.info("封禁规则更新{} - 规则ID: {}", success ? "成功" : "失败", ruleId);
            
            return success;
            
        } catch (Exception e) {
            log.error("更新封禁规则失败 - 规则ID: {}", ruleId, e);
            throw new BusinessException("更新封禁规则失败!");
        }
    }
    
    /**
     * 删除封禁规则
     * 
     * @param appId 项目的App ID
     * @param ruleId 规则ID
     * @return 是否删除成功
     */
    public boolean deleteBanRule(String appId, Long ruleId) {
        try {
            log.info("删除封禁规则 - 规则ID: {}, AppId: {}", ruleId, appId);
            
            BanRuleResponse response = rtcChannelAPIService.deleteBanRule(appId, ruleId,basicAuth);
            
            boolean success = "success".equals(response.getStatus());
            log.info("封禁规则删除{} - 规则ID: {}", success ? "成功" : "失败", ruleId);
            
            return success;
            
        } catch (Exception e) {
            log.error("删除封禁规则失败 - 规则ID: {}", ruleId, e);
            throw new BusinessException("删除封禁规则失败!");
        }
    }
    
    /**
     * 获取所有封禁规则
     * 
     * @param appId 项目的App ID
     * @return 封禁规则列表
     */
    public List<BanRuleInfo> getAllBanRules(String appId) {
        try {
            log.info("获取所有封禁规则 - AppId: {}", appId);
            
            BanRuleListResponse response = rtcChannelAPIService.getBanRuleList(appId,basicAuth);
            
            List<BanRuleInfo> rules = response.getRules();
            int ruleCount = rules != null ? rules.size() : 0;
            
            log.info("获取封禁规则成功 - AppId: {}, 规则数量: {}", appId, ruleCount);
            
            return rules;
            
        } catch (Exception e) {
            log.error("获取封禁规则失败 - AppId: {}", appId, e);
            throw new BusinessException("获取封禁规则失败!");
        }
    }
    
    /**
     * 根据频道名获取封禁规则
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @return 该频道的封禁规则列表
     */
    public List<BanRuleInfo> getBanRulesByChannel(String appId, String channelName) {
        try {
            log.info("获取频道封禁规则 - AppId: {}, 频道名: {}", appId, channelName);
            
            List<BanRuleInfo> allRules = getAllBanRules(appId);
            
            if (allRules == null) {
                return new ArrayList<>();
            }
            
            List<BanRuleInfo> channelRules = allRules.stream()
                .filter(rule -> channelName.equals(rule.getCname()))
                .collect(Collectors.toList());
            
            log.info("频道封禁规则数量 - 频道名: {}, 规则数: {}", channelName, channelRules.size());
            
            return channelRules;
            
        } catch (Exception e) {
            log.error("获取频道封禁规则失败 - 频道名: {}", channelName, e);
            throw new BusinessException("获取频道封禁规则失败!");
        }
    }
    
    /**
     * 根据用户ID获取封禁规则
     * 
     * @param appId 项目的App ID
     * @param userId 用户ID
     * @return 该用户的封禁规则列表
     */
    public List<BanRuleInfo> getBanRulesByUser(String appId, Long userId) {
        try {
            log.info("获取用户封禁规则 - AppId: {}, 用户ID: {}", appId, userId);
            
            List<BanRuleInfo> allRules = getAllBanRules(appId);
            
            if (allRules == null) {
                return new ArrayList<>();
            }
            
            List<BanRuleInfo> userRules = allRules.stream()
                .filter(rule -> userId.equals(rule.getUid()))
                .collect(Collectors.toList());
            
            log.info("用户封禁规则数量 - 用户ID: {}, 规则数: {}", userId, userRules.size());
            
            return userRules;
            
        } catch (Exception e) {
            log.error("获取用户封禁规则失败 - 用户ID: {}", userId, e);
            throw new BusinessException("获取用户封禁规则失败!");
        }
    }
    
    /**
     * 根据权限类型获取封禁规则
     * 
     * @param appId 项目的App ID
     * @param privilege 权限类型（join_channel, publish_audio, publish_video）
     * @return 包含指定权限的封禁规则列表
     */
    public List<BanRuleInfo> getBanRulesByPrivilege(String appId, String privilege) {
        try {
            log.info("获取权限相关封禁规则 - AppId: {}, 权限: {}", appId, privilege);
            
            List<BanRuleInfo> allRules = getAllBanRules(appId);
            
            if (allRules == null) {
                return new ArrayList<>();
            }
            
            List<BanRuleInfo> privilegeRules = allRules.stream()
                .filter(rule -> rule.getPrivileges() != null && rule.getPrivileges().contains(privilege))
                .collect(Collectors.toList());
            
            log.info("权限封禁规则数量 - 权限: {}, 规则数: {}", privilege, privilegeRules.size());
            
            return privilegeRules;
            
        } catch (Exception e) {
            log.error("获取权限封禁规则失败 - 权限: {}", privilege, e);
            throw new BusinessException("获取权限封禁规则失败!");
        }
    }
    
    /**
     * 检查用户是否被封禁指定权限
     * 
     * @param appId 项目的App ID
     * @param userId 用户ID
     * @param channelName 频道名称（可选）
     * @param privilege 权限类型
     * @return 用户是否被封禁指定权限
     */
    public boolean isUserBanned(String appId, Long userId, String channelName, String privilege) {
        try {
            log.info("检查用户封禁状态 - AppId: {}, 用户ID: {}, 频道: {}, 权限: {}", 
                appId, userId, channelName, privilege);
            
            List<BanRuleInfo> userRules = getBanRulesByUser(appId, userId);
            
            if (userRules == null || userRules.isEmpty()) {
                return false;
            }
            
            // 检查是否有匹配的封禁规则
            boolean isBanned = userRules.stream()
                .anyMatch(rule -> {
                    // 检查权限是否匹配
                    boolean privilegeMatch = rule.getPrivileges() != null && 
                        rule.getPrivileges().contains(privilege);
                    
                    // 检查频道是否匹配（如果规则指定了频道）
                    boolean channelMatch = rule.getCname() == null || 
                        rule.getCname().equals(channelName);
                    
                    return privilegeMatch && channelMatch;
                });
            
            log.info("用户封禁状态检查结果 - 用户ID: {}, 权限: {}, 被封禁: {}", 
                userId, privilege, isBanned);
            
            return isBanned;
            
        } catch (Exception e) {
            log.error("检查用户封禁状态失败 - 用户ID: {}, 权限: {}", userId, privilege, e);
            throw new BusinessException("检查用户封禁状态失败!");
        }
    }
    
    /**
     * 清理过期的封禁规则
     * 注意：此方法需要手动检查规则的过期时间，因为API不会自动清理
     * 
     * @param appId 项目的App ID
     * @return 清理的规则数量
     */
    public int cleanExpiredBanRules(String appId) {
        try {
            log.info("开始清理过期封禁规则 - AppId: {}", appId);
            
            List<BanRuleInfo> allRules = getAllBanRules(appId);
            
            if (allRules == null || allRules.isEmpty()) {
                log.info("没有封禁规则需要清理");
                return 0;
            }
            
            long currentTime = System.currentTimeMillis();
            int cleanedCount = 0;
            
            for (BanRuleInfo rule : allRules) {
                try {
                    // 计算规则是否过期
                    if (isRuleExpired(rule, currentTime)) {
                        boolean deleted = deleteBanRule(appId, rule.getId());
                        if (deleted) {
                            cleanedCount++;
                            log.info("清理过期规则 - 规则ID: {}", rule.getId());
                        }
                    }
                } catch (Exception e) {
                    log.error("清理规则失败 - 规则ID: {}", rule.getId(), e);
                }
            }
            
            log.info("过期规则清理完成 - 清理数量: {}", cleanedCount);
            return cleanedCount;
            
        } catch (Exception e) {
            log.error("清理过期封禁规则失败 - AppId: {}", appId, e);
            throw new BusinessException("清理过期封禁规则失败!");
        }
    }
    
    /**
     * 批量删除封禁规则
     * 
     * @param appId 项目的App ID
     * @param ruleIds 规则ID列表
     * @return 成功删除的规则数量
     */
    public int batchDeleteBanRules(String appId, List<Long> ruleIds) {
        if (ruleIds == null || ruleIds.isEmpty()) {
            return 0;
        }
        
        log.info("批量删除封禁规则 - AppId: {}, 规则数量: {}", appId, ruleIds.size());
        
        int successCount = 0;
        for (Long ruleId : ruleIds) {
            try {
                boolean deleted = deleteBanRule(appId, ruleId);
                if (deleted) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("批量删除失败 - 规则ID: {}", ruleId, e);
            }
        }
        
        log.info("批量删除完成 - 总数: {}, 成功: {}", ruleIds.size(), successCount);
        return successCount;
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 检查规则是否过期
     */
    private boolean isRuleExpired(BanRuleInfo rule, long currentTime) {
        if (rule.getCreateAt() == null || rule.getTs() == null) {
            return false;
        }
        
        // 规则过期时间 = 创建时间 + 封禁时长（毫秒）
        long expireTime = rule.getCreateAt() + (rule.getTs() * 1000L);
        
        return currentTime > expireTime;
    }
}