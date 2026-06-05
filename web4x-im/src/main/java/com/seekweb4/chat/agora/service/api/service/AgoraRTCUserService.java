package com.seekweb4.chat.agora.service.api.service;

import com.seekweb4.chat.agora.bean.dto.UserStatusResponseDto;
import com.seekweb4.chat.agora.bean.exception.BusinessException;
import com.seekweb4.chat.agora.service.api.RtcChannelAPIService;
import com.seekweb4.chat.agora.service.api.request.BanRuleCreateRequest;
import com.seekweb4.chat.agora.service.api.response.BanRuleInfo;
import com.seekweb4.chat.agora.service.api.response.BanRuleListResponse;
import com.seekweb4.chat.agora.service.api.response.BanRuleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 声网RTC用户管理服务类
 * 提供用户的禁言、解除禁言、踢出等用户管理功能
 * 
 * @author liangbo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgoraRTCUserService {
    
    private final RtcChannelAPIService rtcChannelAPIService;

    @Value("${whitelist.rtcKickOutAuth.basicAuth}")
    private String basicAuth;


    /**
     * 禁言指定频道中的用户
     * 禁止用户发布音频和视频，但允许其继续在频道中收听/观看
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称，如果为null则在所有频道禁言
     * @param userId 用户ID
     * @param banDurationMinutes 禁言时长（分钟）
     * @return 封禁规则ID
     */
    public Long muteUser(String appId, String channelName, Long userId, int banDurationMinutes) {
        try {
            log.info("禁言用户 - AppId: {}, 频道名: {}, 用户ID: {}, 禁言时长: {}分钟", 
                appId, channelName, userId, banDurationMinutes);
            
            // 检查用户是否在频道中（如果指定了频道）
            if (channelName != null && !isUserInChannel(appId, channelName, userId)) {
                log.warn("用户不在指定频道中 - 用户ID: {}, 频道名: {}", userId, channelName);
                // 即使用户不在频道中，仍然创建禁言规则以防止用户进入后发言
            }
            
            // 创建禁言规则（禁止发布音频和视频）
            BanRuleCreateRequest request = new BanRuleCreateRequest();
            request.setAppid(appId);
            request.setCname(channelName); // 如果为null，则在所有频道生效
            request.setUid(userId);
            request.setTime(banDurationMinutes);
            request.setPrivileges(Arrays.asList("publish_audio", "publish_video"));
            
            BanRuleResponse response = rtcChannelAPIService.createBanRule(request,basicAuth);
            
            String scope = channelName != null ? "频道 " + channelName : "所有频道";
            log.info("用户已被禁言 - 用户ID: {}, 作用范围: {}, 封禁规则ID: {}", 
                userId, scope, response.getId());
            
            return response.getId();
            
        } catch (Exception e) {
            log.error("禁言用户失败 - 用户ID: {}, 频道名: {}", userId, channelName, e);
            throw new BusinessException("禁言用户失败!");
        }
    }
    
    /**
     * 解除指定频道中用户的禁言
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param userId 用户ID
     * @return 是否成功解除
     */
    public boolean unmuteUser(String appId, String channelName, Long userId) {
        try {
            log.info("解除用户禁言 - AppId: {}, 频道名: {}, 用户ID: {}", appId, channelName, userId);
            
            // 获取该用户的所有封禁规则
            BanRuleListResponse ruleList = rtcChannelAPIService.getBanRuleList(appId,basicAuth);
            
            if (ruleList.getRules() == null) {
                log.info("未找到任何封禁规则 - 用户ID: {}", userId);
                return true;
            }
            
            // 找到匹配的禁言规则（publish_audio 或 publish_video）
            List<BanRuleInfo> muteRules = ruleList.getRules().stream()
                .filter(rule -> userId.equals(rule.getUid()))
                .filter(rule -> channelName == null || channelName.equals(rule.getCname()))
                .filter(rule -> rule.getPrivileges() != null && 
                    (rule.getPrivileges().contains("publish_audio") || 
                     rule.getPrivileges().contains("publish_video")))
                .collect(Collectors.toList());
            
            if (muteRules.isEmpty()) {
                log.info("未找到用户的禁言规则 - 用户ID: {}, 频道名: {}", userId, channelName);
                return true;
            }
            
            // 删除找到的禁言规则
            boolean allSuccess = true;
            for (BanRuleInfo rule : muteRules) {
                try {
                    BanRuleResponse response = rtcChannelAPIService.deleteBanRule(appId, rule.getId(),basicAuth);
                    log.info("已删除禁言规则 - 规则ID: {}, 用户ID: {}", rule.getId(), userId);
                } catch (Exception e) {
                    log.error("删除禁言规则失败 - 规则ID: {}, 用户ID: {}", rule.getId(), userId, e);
                    allSuccess = false;
                }
            }
            
            if (allSuccess) {
                log.info("用户禁言已解除 - 用户ID: {}, 频道名: {}", userId, channelName);
            }
            
            return allSuccess;
            
        } catch (Exception e) {
            log.error("解除用户禁言失败 - 用户ID: {}, 频道名: {}", userId, channelName, e);
            throw new BusinessException("解除用户禁言失败!");
        }
    }
    
    /**
     * 踢出用户并禁止其重新加入
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称，如果为null则在所有频道禁止
     * @param userId 用户ID
     * @param banDurationMinutes 禁止重新加入的时长（分钟）
     * @return 封禁规则ID
     */
    public Long kickUser(String appId, String channelName, Long userId, int banDurationMinutes) {
        try {
            log.info("踢出用户 - AppId: {}, 频道名: {}, 用户ID: {}, 禁止时长: {}分钟", 
                appId, channelName, userId, banDurationMinutes);
            
            // 检查用户是否在频道中
            if (channelName != null && !isUserInChannel(appId, channelName, userId)) {
                log.warn("用户不在指定频道中 - 用户ID: {}, 频道名: {}", userId, channelName);
            }
            
            // 创建踢出规则
            BanRuleCreateRequest request = new BanRuleCreateRequest();
            request.setAppid(appId);
            request.setCname(channelName);
            request.setUid(userId);
            request.setTime(banDurationMinutes);
            request.setPrivileges(Arrays.asList("join_channel"));
            
            BanRuleResponse response = rtcChannelAPIService.createBanRule(request,basicAuth);
            
            String scope = channelName != null ? "频道 " + channelName : "所有频道";
            log.info("用户已被踢出 - 用户ID: {}, 作用范围: {}, 封禁规则ID: {}", 
                userId, scope, response.getId());
            
            return response.getId();
            
        } catch (Exception e) {
            log.error("踢出用户失败 - 用户ID: {}, 频道名: {}", userId, channelName, e);
            throw new BusinessException("踢出用户失败!");
        }
    }
    
    /**
     * 解除用户的频道禁入
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param userId 用户ID
     * @return 是否成功解除
     */
    public boolean unkickUser(String appId, String channelName, Long userId) {
        try {
            log.info("解除用户踢出 - AppId: {}, 频道名: {}, 用户ID: {}", appId, channelName, userId);
            
            // 获取该用户的所有封禁规则
            BanRuleListResponse ruleList = rtcChannelAPIService.getBanRuleList(appId,basicAuth);
            
            if (ruleList.getRules() == null) {
                log.info("未找到任何封禁规则 - 用户ID: {}", userId);
                return true;
            }
            
            // 找到匹配的踢出规则（join_channel）
            List<BanRuleInfo> kickRules = ruleList.getRules().stream()
                .filter(rule -> userId.equals(rule.getUid()))
                .filter(rule -> channelName == null || channelName.equals(rule.getCname()))
                .filter(rule -> rule.getPrivileges() != null && 
                    rule.getPrivileges().contains("join_channel"))
                .collect(Collectors.toList());
            
            if (kickRules.isEmpty()) {
                log.info("未找到用户的踢出规则 - 用户ID: {}, 频道名: {}", userId, channelName);
                return true;
            }
            
            // 删除找到的踢出规则
            boolean allSuccess = true;
            for (BanRuleInfo rule : kickRules) {
                try {
                    BanRuleResponse response = rtcChannelAPIService.deleteBanRule(appId, rule.getId(),basicAuth);
                    log.info("已删除踢出规则 - 规则ID: {}, 用户ID: {}", rule.getId(), userId);
                } catch (Exception e) {
                    log.error("删除踢出规则失败 - 规则ID: {}, 用户ID: {}", rule.getId(), userId, e);
                    allSuccess = false;
                }
            }
            
            if (allSuccess) {
                log.info("用户踢出已解除 - 用户ID: {}, 频道名: {}", userId, channelName);
            }
            
            return allSuccess;
            
        } catch (Exception e) {
            log.error("解除用户踢出失败 - 用户ID: {}, 频道名: {}", userId, channelName, e);
            throw new BusinessException("解除用户踢出失败!");
        }
    }
    
    /**
     * 查询用户在指定频道的状态
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param userId 用户ID
     * @return 用户是否在线
     */
    public boolean isUserOnline(String appId, String channelName, Long userId) {
        try {
            log.info("查询用户状态 - AppId: {}, 频道名: {}, 用户ID: {}", appId, channelName, userId);
            
            UserStatusResponseDto response = rtcChannelAPIService.getUserStatus(appId, userId.toString(), channelName,basicAuth);
            
            boolean isOnline = response.getData() != null && Boolean.TRUE.equals(response.getData().getOnline());
            log.info("用户状态查询结果 - 用户ID: {}, 频道名: {}, 在线状态: {}", userId, channelName, isOnline);
            
            return isOnline;
            
        } catch (Exception e) {
            log.error("查询用户状态失败 - 用户ID: {}, 频道名: {}", userId, channelName, e);
            throw new BusinessException("查询用户状态失败!");
        }
    }
    
    /**
     * 获取用户的所有封禁规则
     * 
     * @param appId 项目的App ID
     * @param userId 用户ID
     * @return 用户的封禁规则列表
     */
    public List<BanRuleInfo> getUserBanRules(String appId, Long userId) {
        try {
            log.info("获取用户封禁规则 - AppId: {}, 用户ID: {}", appId, userId);
            
            BanRuleListResponse ruleList = rtcChannelAPIService.getBanRuleList(appId,basicAuth);
            
            if (ruleList.getRules() == null) {
                return Arrays.asList();
            }
            
            List<BanRuleInfo> userRules = ruleList.getRules().stream()
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
     * 批量禁言用户
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param userIds 用户ID列表
     * @param banDurationMinutes 禁言时长（分钟）
     * @return 成功创建的规则数量
     */
    public int batchMuteUsers(String appId, String channelName, List<Long> userIds, int banDurationMinutes) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }
        
        log.info("批量禁言用户 - AppId: {}, 频道名: {}, 用户数: {}, 禁言时长: {}分钟", 
            appId, channelName, userIds.size(), banDurationMinutes);
        
        int successCount = 0;
        for (Long userId : userIds) {
            try {
                muteUser(appId, channelName, userId, banDurationMinutes);
                successCount++;
            } catch (Exception e) {
                log.error("批量禁言失败 - 用户ID: {}", userId, e);
            }
        }
        
        log.info("批量禁言完成 - 总数: {}, 成功: {}", userIds.size(), successCount);
        return successCount;
    }
    
    /**
     * 批量踢出用户
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param userIds 用户ID列表
     * @param banDurationMinutes 禁止重新加入的时长（分钟）
     * @return 成功创建的规则数量
     */
    public int batchKickUsers(String appId, String channelName, List<Long> userIds, int banDurationMinutes) {
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }
        
        log.info("批量踢出用户 - AppId: {}, 频道名: {}, 用户数: {}, 禁止时长: {}分钟", 
            appId, channelName, userIds.size(), banDurationMinutes);
        
        int successCount = 0;
        for (Long userId : userIds) {
            try {
                kickUser(appId, channelName, userId, banDurationMinutes);
                successCount++;
            } catch (Exception e) {
                log.error("批量踢出失败 - 用户ID: {}", userId, e);
            }
        }
        
        log.info("批量踢出完成 - 总数: {}, 成功: {}", userIds.size(), successCount);
        return successCount;
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 检查用户是否在频道中
     */
    private boolean isUserInChannel(String appId, String channelName, Long userId) {
        try {
            return isUserOnline(appId, channelName, userId);
        } catch (Exception e) {
            log.error("检查用户是否在频道失败 - 用户ID: {}, 频道名: {}", userId, channelName, e);
            return false;
        }
    }
}