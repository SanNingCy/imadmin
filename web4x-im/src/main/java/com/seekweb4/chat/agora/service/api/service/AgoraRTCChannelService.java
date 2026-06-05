package com.seekweb4.chat.agora.service.api.service;


import com.seekweb4.chat.agora.bean.dto.UserListResponseDto;
import com.seekweb4.chat.agora.bean.exception.BusinessException;
import com.seekweb4.chat.agora.service.api.RtcChannelAPIService;
import com.seekweb4.chat.agora.service.api.request.BanRuleCreateRequest;
import com.seekweb4.chat.agora.service.api.response.BanRuleResponse;
import com.seekweb4.chat.agora.service.api.response.ChannelListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 声网RTC频道管理服务类
 * 提供频道的创建、解散、用户管理等高级功能
 * 
 * @author liangbo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgoraRTCChannelService {
    
    private final RtcChannelAPIService rtcChannelAPIService;

    @Value("${whitelist.rtcKickOutAuth.basicAuth}")
    private String basicAuth;

    /**
     * 创建频道
     * 注意：RTC频道是动态创建的，当第一个用户加入时频道自动创建
     * 此方法主要用于验证频道状态和配置
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @return 频道是否可用
     */
    public boolean createChannel(String appId, String channelName) {
        try {
            log.info("检查频道状态 - AppId: {}, 频道名: {}", appId, channelName);
            
            // 检查频道是否已存在
            UserListResponseDto userList = rtcChannelAPIService.getUserList(appId, channelName,basicAuth);
            
            if (userList.getData() != null && Boolean.TRUE.equals(userList.getData().getChannelExist())) {
                log.info("频道已存在 - 频道名: {}, 用户数: {}", channelName, 
                    userList.getData().getTotal() != null ? userList.getData().getTotal() : 0);
                return true;
            } else {
                log.info("频道未创建，将在第一个用户加入时自动创建 - 频道名: {}", channelName);
                return true; // RTC频道会在用户加入时自动创建
            }
            
        } catch (Exception e) {
            log.error("检查频道状态失败 - 频道名: {}", channelName, e);
            throw new BusinessException("检查频道状态失败!");
        }
    }
    
    /**
     * 解散频道
     * 通过踢出所有用户并禁止新用户加入来实现频道解散
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param banDurationMinutes 禁止加入的时长（分钟），0表示永久禁止
     * @return 是否成功解散
     */
    public boolean dissolveChannel(String appId, String channelName, int banDurationMinutes) {
        try {
            log.info("开始解散频道 - AppId: {}, 频道名: {}, 禁止时长: {}分钟", 
                appId, channelName, banDurationMinutes);
            
            // 1. 获取频道中的所有用户
            UserListResponseDto userList = rtcChannelAPIService.getUserList(appId, channelName,basicAuth);
            
            if (userList.getData() == null || !Boolean.TRUE.equals(userList.getData().getChannelExist())) {
                log.info("频道不存在或已为空 - 频道名: {}", channelName);
                return true;
            }
            
            // 2. 踢出所有用户（如果有用户在频道中）
            List<Long> allUsers = getAllUsersInChannel(userList);
            if (!CollectionUtils.isEmpty(allUsers)) {
                log.info("频道中有 {} 个用户，开始踢出所有用户", allUsers.size());
                
                for (Long userId : allUsers) {
                    kickUserFromChannel(appId, channelName, userId, 1); // 踢出用户1分钟
                }
            }
            
            // 3. 创建频道级别的封禁规则，禁止任何人加入此频道
            BanRuleCreateRequest banRequest = new BanRuleCreateRequest();
            banRequest.setAppid(appId);
            banRequest.setCname(channelName);
            banRequest.setTime(banDurationMinutes > 0 ? banDurationMinutes : 1440); // 最大1440分钟
            banRequest.setPrivileges(Arrays.asList("join_channel"));
            
            BanRuleResponse banResponse = rtcChannelAPIService.createBanRule(banRequest,basicAuth);
            
            log.info("频道解散成功 - 频道名: {}, 封禁规则ID: {}", channelName, banResponse.getId());
            return true;
            
        } catch (Exception e) {
            log.error("解散频道失败 - 频道名: {}", channelName, e);
            throw new BusinessException("解散频道失败!");
        }
    }
    
    /**
     * 从频道踢出指定用户
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @param userId 用户ID
     * @param banDurationMinutes 禁止重新加入的时长（分钟）
     * @return 封禁规则ID
     */
    public Long kickUserFromChannel(String appId, String channelName, Long userId, int banDurationMinutes) {
        try {
            log.info("踢出用户 - AppId: {}, 频道名: {}, 用户ID: {}, 禁止时长: {}分钟", 
                appId, channelName, userId, banDurationMinutes);
            
            // 检查用户是否在频道中
            if (!isUserInChannel(appId, channelName, userId)) {
                log.warn("用户不在频道中 - 用户ID: {}, 频道名: {}", userId, channelName);
                return null;
            }
            
            // 创建封禁规则踢出用户
            BanRuleCreateRequest request = new BanRuleCreateRequest();
            request.setAppid(appId);
            request.setCname(channelName);
            request.setUid(userId);
            request.setTime(banDurationMinutes);
            request.setPrivileges(Arrays.asList("join_channel"));
            
            BanRuleResponse response = rtcChannelAPIService.createBanRule(request,basicAuth);
            
            log.info("用户已被踢出 - 用户ID: {}, 频道名: {}, 封禁规则ID: {}", 
                userId, channelName, response.getId());
            
            return response.getId();
            
        } catch (Exception e) {
            log.error("踢出用户失败 - 用户ID: {}, 频道名: {}", userId, channelName, e);
            throw new BusinessException("踢出用户失败!");
        }
    }
    
    /**
     * 获取频道中的所有用户
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @return 用户ID列表
     */
    public List<Long> getChannelUsers(String appId, String channelName) {
        try {
            log.info("获取频道用户列表 - AppId: {}, 频道名: {}", appId, channelName);

            UserListResponseDto userList = rtcChannelAPIService.getUserList(appId, channelName,basicAuth);
            
            if (userList.getData() == null || !Boolean.TRUE.equals(userList.getData().getChannelExist())) {
                log.info("频道不存在 - 频道名: {}", channelName);
                return Arrays.asList();
            }
            
            List<Long> allUsers = getAllUsersInChannel(userList);
            log.info("频道用户数量 - 频道名: {}, 用户数: {}", channelName, allUsers.size());
            
            return allUsers;
            
        } catch (Exception e) {
            log.error("获取频道用户列表失败 - 频道名: {}", channelName, e);
            throw new BusinessException("获取频道用户列表失败!");
        }
    }
    
    /**
     * 检查频道是否存在
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @return 频道是否存在
     */
    public boolean channelExists(String appId, String channelName) {
        try {
            UserListResponseDto userList = rtcChannelAPIService.getUserList(appId, channelName,basicAuth);
            return userList.getData() != null && Boolean.TRUE.equals(userList.getData().getChannelExist());
        } catch (Exception e) {
            log.error("检查频道存在性失败 - 频道名: {}", channelName, e);
            return false;
        }
    }
    
    /**
     * 获取频道信息
     * 
     * @param appId 项目的App ID
     * @param channelName 频道名称
     * @return 用户列表响应
     */
    public UserListResponseDto getChannelInfo(String appId, String channelName) {
        try {
            log.info("获取频道信息 - AppId: {}, 频道名: {}", appId, channelName);
            return rtcChannelAPIService.getUserList(appId, channelName,basicAuth);
        } catch (Exception e) {
            log.error("获取频道信息失败 - 频道名: {}", channelName, e);
            throw new BusinessException("获取频道信息失败!");
        }
    }
    
    /**
     * 获取项目下所有频道
     * 
     * @param appId 项目的App ID
     * @param pageNo 页码（从0开始）
     * @param pageSize 每页大小
     * @return 频道列表响应
     */
    public ChannelListResponse getAllChannels(String appId, Integer pageNo, Integer pageSize) {
        try {
            log.info("获取所有频道 - AppId: {}, 页码: {}, 每页大小: {}", appId, pageNo, pageSize);
            return rtcChannelAPIService.getChannelList(appId, pageNo, pageSize,basicAuth);
        } catch (Exception e) {
            log.error("获取频道列表失败 - AppId: {}", appId, e);
            throw new BusinessException("获取频道列表失败!");
        }
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 检查用户是否在频道中
     */
    private boolean isUserInChannel(String appId, String channelName, Long userId) {
        try {
            UserListResponseDto userList = rtcChannelAPIService.getUserList(appId, channelName,basicAuth);
            
            if (userList.getData() == null || !Boolean.TRUE.equals(userList.getData().getChannelExist())) {
                return false;
            }
            
            List<Long> allUsers = getAllUsersInChannel(userList);
            return allUsers.contains(userId);
            
        } catch (Exception e) {
            log.error("检查用户是否在频道失败 - 用户ID: {}, 频道名: {}", userId, channelName, e);
            return false;
        }
    }
    
    /**
     * 获取频道中的所有用户（包括主播和观众）
     */
    private List<Long> getAllUsersInChannel(UserListResponseDto userList) {
        List<Long> allUsers = Arrays.asList();
        
        if (userList.getData() == null) {
            return allUsers;
        }
        
        // 通信模式：返回所有用户
        if (userList.getData().getMode() != null && userList.getData().getMode() == 1) {
            allUsers = userList.getData().getUsers() != null ? 
                userList.getData().getUsers() : Arrays.asList();
        }
        // 直播模式：合并主播和观众
        else if (userList.getData().getMode() != null && userList.getData().getMode() == 2) {
            allUsers = Arrays.asList();
            
            if (userList.getData().getBroadcasters() != null) {
                allUsers.addAll(userList.getData().getBroadcasters());
            }
            
            if (userList.getData().getAudience() != null) {
                allUsers.addAll(userList.getData().getAudience());
            }
        }
        
        return allUsers;
    }
}