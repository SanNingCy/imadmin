package com.seekweb4.chat.agora.service.impl;

import com.seekweb4.chat.agora.bean.config.RTCKickOutAuthConfig;
import com.seekweb4.chat.agora.bean.dto.KickOutRuleDto;
import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import com.seekweb4.chat.agora.bean.enums.ReturnCodeEnum;
import com.seekweb4.chat.agora.bean.enums.RtcChannelRulesEnum;
import com.seekweb4.chat.agora.bean.exception.BusinessException;
import com.seekweb4.chat.agora.bean.req.UserKickOutReq;
import com.seekweb4.chat.agora.config.WhitelistConfig;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.service.IRtcChannelService;
import com.seekweb4.chat.agora.service.IAgoraUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 用户服务实现类
 * 
 * <p>用户服务的具体实现，主要处理用户相关的业务操作。</p>
 * <p>使用白名单配置管理不同项目的认证信息。</p>
 * <p>支持频道管理员管理和被踢出用户管理功能，数据存储在RoomListV2Entity的payload字段中。</p>
 * 
 * @author Agora
 * @see IAgoraUserService
 * @see IRtcChannelService
 */
@Slf4j
@Service
public class UserServiceImpl implements IAgoraUserService {
    
    /** RTC频道服务，用于执行底层的踢出操作 */
    @Resource
    private IRtcChannelService rtcChannelService;

    /** 白名单配置，管理项目认证信息 */
    @Resource
    private WhitelistConfig whitelistConfig;
    
    /** 房间列表V2仓库，用于管理员和踢出用户数据存储 */
    @Resource
    private RoomListV2Repository roomListV2Repository;
    
    /** MongoDB模板，用于复杂查询和更新操作 */
    @Resource
    private MongoTemplate mongoTemplate;

    // 常量定义
    private static final String PAYLOAD_ADMINS = "admins";
    private static final String PAYLOAD_KICKED_USERS = "kickedUsers";
    private static final String ADMIN_USER_ID = "userId";
    private static final String ADMIN_CREATE_TIME = "createTime";
    private static final String ADMIN_OPERATOR = "operator";
    private static final String KICKED_USER_ID = "userId";
    private static final String KICKED_USER_NAME = "userName";
    private static final String KICKED_TIME = "kickTime";
    private static final String KICKED_OPERATOR = "operator";
    private static final String KICKED_REASON = "reason";

    /**
     * 踢出用户
     * 
     * <p>将指定用户从频道中踢出，并创建相应的封禁规则。</p>
     * <p>认证信息优先使用白名单配置，其次使用请求中的认证信息。</p>
     * 
     * <p><b>踢出规则：</b></p>
     * <ul>
     *   <li>封禁时长：60分钟</li>
     *   <li>封禁权限：禁止加入频道（JOIN_CHANNEL）</li>
     * </ul>
     * 
     * @param req 用户踢出请求参数，包含用户ID、房间ID等信息
     * @return 踢出规则创建结果
     * @throws Exception 踢出操作失败时抛出异常
     */
    @Override
    public KickOutRuleDto kickOut(UserKickOutReq req) throws Exception {
        // 从白名单配置中获取认证信息
        RTCKickOutAuthConfig config = whitelistConfig.getRtcKickOutAuthFromWhitelist(req.getAppId());
        
        // 检查认证信息是否可用
        if (config == null && req.getBasicAuth() == null) {
            log.info("kick out user:{},cname:{} failed,rtc kick out auth from whitelist", req.getUid(), req.getRoomId());
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.USER_KICK_OUT_AUTH_NOT_FOUND_ERROR);
        } else {
            // 优先使用白名单配置中的认证信息
            if (config != null) {
                req.setBasicAuth(config.getBasicAuth());
            }
        }

        try {
            // 执行踢出操作：封禁60分钟，禁止加入频道
            rtcChannelService.kickOut(req.getBasicAuth(), req.getAppId(), req.getRoomId(), req.getUid(), 60, Arrays.asList(RtcChannelRulesEnum.JOIN_CHANNEL.getRule()));
            log.info("kick out user:{},cname:{} successfully", req.getUid(), req.getRoomId());
            return new KickOutRuleDto().setUid(req.getUid());
        } catch (Exception ex) {
            log.info("failed to kick out user:{},cname:{},err:{}", req.getUid(), req.getRoomId(), ex.toString());
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.USER_KICK_OUT_ERROR);
        }
    }

    // ==================== 频道管理员管理实现 ====================
    
    @Override
    @Transactional
    public boolean addRoomIdAdmin(String appId, String roomId, String userId, String operator) throws Exception {
        log.info("Adding channel admin - appId: {}, roomId: {}, userId: {}, operator: {}", appId, roomId, userId, operator);
        
        Query query = buildRoomQuery(appId, roomId);
        RoomListV2Entity room = mongoTemplate.findOne(query, RoomListV2Entity.class);
        
        if (room == null) {
            room = createRoomEntity(appId, roomId);
        }
        
        Map<String, Object> payload = room.getPayload() != null ? room.getPayload() : new HashMap<>(); 
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> admins = (List<Map<String, Object>>) payload.get(PAYLOAD_ADMINS);
        if (admins == null) {
            admins = new ArrayList<>();
        }
        
        // 检查用户是否已经是管理员
        boolean isExisting = admins.stream().anyMatch(admin -> userId.equals(admin.get(ADMIN_USER_ID)));
        if (isExisting) {
            log.info("User {} is already admin for channel {}", userId, roomId);
            return false;
        }
        
        // 添加新管理员
        Map<String, Object> newAdmin = new HashMap<>();
        newAdmin.put(ADMIN_USER_ID, userId);
        newAdmin.put(ADMIN_CREATE_TIME, System.currentTimeMillis());
        newAdmin.put(ADMIN_OPERATOR, operator);
        admins.add(newAdmin);
        
        payload.put(PAYLOAD_ADMINS, admins);
        
        // 更新数据库
        Update update = new Update()
            .set("payload", payload)
            .set("updateTime", System.currentTimeMillis());
        
        mongoTemplate.updateFirst(query, update, RoomListV2Entity.class);
        log.info("Successfully added channel admin - userId: {}, roomId: {}", userId, roomId);
        return true;
    }
    
    @Override
    @Transactional
    public boolean removeRoomIdAdmin(String appId, String roomId, String userId, String operator) throws Exception {
        log.info("Removing channel admin - appId: {}, roomId: {}, userId: {}, operator: {}", appId, roomId, userId, operator);
        
        Query query = buildRoomQuery(appId, roomId);
        RoomListV2Entity room = mongoTemplate.findOne(query, RoomListV2Entity.class);
        
        if (room == null || room.getPayload() == null) {
            log.info("Room not found or no payload data for roomId: {}", roomId);
            return false;
        }
        
        Map<String, Object> payload = room.getPayload();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> admins = (List<Map<String, Object>>) payload.get(PAYLOAD_ADMINS);
        
        if (admins == null || admins.isEmpty()) {
            log.info("No admins found for channel: {}", roomId);
            return false;
        }
        
        // 移除指定管理员
        boolean removed = admins.removeIf(admin -> userId.equals(admin.get(ADMIN_USER_ID)));
        
        if (!removed) {
            log.info("User {} is not admin for channel {}", userId, roomId);
            return false;
        }
        
        payload.put(PAYLOAD_ADMINS, admins);
        
        // 更新数据库
        Update update = new Update()
            .set("payload", payload)
            .set("updateTime", System.currentTimeMillis());
        
        mongoTemplate.updateFirst(query, update, RoomListV2Entity.class);
        log.info("Successfully removed channel admin - userId: {}, roomId: {}", userId, roomId);
        return true;
    }
    
    @Override
    public List<Map<String, Object>> getRoomIdAdmins(String appId, String roomId) throws Exception {
        log.info("Getting channel admins - appId: {}, roomId: {}", appId, roomId);
        
        Query query = buildRoomQuery(appId, roomId);
        RoomListV2Entity room = mongoTemplate.findOne(query, RoomListV2Entity.class);
        
        if (room == null || room.getPayload() == null) {
            return new ArrayList<>();
        }
        
        Map<String, Object> payload = room.getPayload();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> admins = (List<Map<String, Object>>) payload.get(PAYLOAD_ADMINS);
        
        if (admins == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(admins);
    }
    
    @Override
    public boolean isRoomIdAdmin(String appId, String roomId, String userId) throws Exception {
        log.info("Checking if user is channel admin - appId: {}, roomId: {}, userId: {}", appId, roomId, userId);
        
        Query query = buildRoomQuery(appId, roomId);
        RoomListV2Entity room = mongoTemplate.findOne(query, RoomListV2Entity.class);
        
        if (room == null || room.getPayload() == null) {
            return false;
        }
        
        Map<String, Object> payload = room.getPayload();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> admins = (List<Map<String, Object>>) payload.get(PAYLOAD_ADMINS);
        
        if (admins == null) {
            return false;
        }
        
        return admins.stream().anyMatch(admin -> userId.equals(admin.get(ADMIN_USER_ID)));
    }
    
    @Override
    @Transactional
    public Map<String, Object> batchAddRoomIdAdmins(String appId, String roomId, List<String> userIds, String operator) throws Exception {
        log.info("Batch adding channel admins - appId: {}, roomId: {}, userIds: {}, operator: {}", appId, roomId, userIds, operator);
        
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> failedUserIds = new ArrayList<>();
        
        for (String userId : userIds) {
            try {
                boolean added = addRoomIdAdmin(appId, roomId, userId, operator);
                if (added) {
                    successCount++;
                } else {
                    failCount++;
                    failedUserIds.add(userId);
                }
            } catch (Exception e) {
                log.error("Failed to add admin {}: {}", userId, e.getMessage());
                failCount++;
                failedUserIds.add(userId);
            }
        }
        
        result.put("total", userIds.size());
        result.put("success", successCount);
        result.put("failed", failCount);
        result.put("failedUserIds", failedUserIds);
        
        log.info("Batch add admins completed - total: {}, success: {}, failed: {}", userIds.size(), successCount, failCount);
        return result;
    }
    
    // ==================== 被踢出用户管理实现 ====================
    
    @Override
    @Transactional
    public boolean addKickedUser(String appId, String roomId, String userId, String userName, String operator, String reason) throws Exception {
        log.info("Adding kicked user - appId: {}, roomId: {}, userId: {}, userName: {}, operator: {}, reason: {}", 
                appId, roomId, userId, userName, operator, reason);
        
        Query query = buildRoomQuery(appId, roomId);
        RoomListV2Entity room = mongoTemplate.findOne(query, RoomListV2Entity.class);
        
        if (room == null) {
            room = createRoomEntity(appId, roomId);
        }
        
        Map<String, Object> payload = room.getPayload() != null ? room.getPayload() : new HashMap<>(); 
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> kickedUsers = (List<Map<String, Object>>) payload.get(PAYLOAD_KICKED_USERS);
        if (kickedUsers == null) {
            kickedUsers = new ArrayList<>();
        }
        
        // 检查用户是否已经在被踢出列表中
        boolean isExisting = kickedUsers.stream().anyMatch(user -> userId.equals(user.get(KICKED_USER_ID)));
        if (isExisting) {
            log.info("User {} is already in kicked list for room {}", userId, roomId);
            return false;
        }
        
        // 添加被踢出用户记录
        Map<String, Object> kickedUser = new HashMap<>();
        kickedUser.put(KICKED_USER_ID, userId);
        kickedUser.put(KICKED_USER_NAME, userName);
        kickedUser.put(KICKED_TIME, System.currentTimeMillis());
        kickedUser.put(KICKED_OPERATOR, operator);
        kickedUser.put(KICKED_REASON, reason != null ? reason : "No reason provided");
        kickedUsers.add(kickedUser);
        
        payload.put(PAYLOAD_KICKED_USERS, kickedUsers);
        
        // 更新数据库
        Update update = new Update()
            .set("payload", payload)
            .set("updateTime", System.currentTimeMillis());
        
        mongoTemplate.updateFirst(query, update, RoomListV2Entity.class);
        log.info("Successfully added kicked user - userId: {}, userName: {}, roomId: {}", userId, userName, roomId);
        return true;
    }
    
    @Override
    @Transactional
    public boolean removeKickedUser(String appId, String roomId, String userId, String operator) throws Exception {
        log.info("Removing kicked user - appId: {}, roomId: {}, userId: {}, operator: {}", appId, roomId, userId, operator);
        
        Query query = buildRoomQuery(appId, roomId);
        RoomListV2Entity room = mongoTemplate.findOne(query, RoomListV2Entity.class);
        
        if (room == null || room.getPayload() == null) {
            log.info("Room not found or no payload data for roomId: {}", roomId);
            return false;
        }
        
        Map<String, Object> payload = room.getPayload();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> kickedUsers = (List<Map<String, Object>>) payload.get(PAYLOAD_KICKED_USERS);
        
        if (kickedUsers == null || kickedUsers.isEmpty()) {
            log.info("No kicked users found for room: {}", roomId);
            return false;
        }
        
        // 移除指定踢出用户
        boolean removed = kickedUsers.removeIf(user -> userId.equals(user.get(KICKED_USER_ID)));
        
        if (!removed) {
            log.info("User {} is not in kicked list for room {}", userId, roomId);
            return false;
        }
        
        payload.put(PAYLOAD_KICKED_USERS, kickedUsers);
        
        // 更新数据库
        Update update = new Update()
            .set("payload", payload)
            .set("updateTime", System.currentTimeMillis());
        
        mongoTemplate.updateFirst(query, update, RoomListV2Entity.class);
        log.info("Successfully removed kicked user - userId: {}, roomId: {}", userId, roomId);
        return true;
    }
    
    @Override
    public List<Map<String, Object>> getKickedUsers(String appId, String roomId) throws Exception {
        log.info("Getting kicked users - appId: {}, roomId: {}", appId, roomId);
        
        Query query = buildRoomQuery(appId, roomId);
        RoomListV2Entity room = mongoTemplate.findOne(query, RoomListV2Entity.class);
        
        if (room == null || room.getPayload() == null) {
            return new ArrayList<>();
        }
        
        Map<String, Object> payload = room.getPayload();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> kickedUsers = (List<Map<String, Object>>) payload.get(PAYLOAD_KICKED_USERS);
        
        if (kickedUsers == null) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(kickedUsers);
    }
    
    @Override
    public boolean isUserKicked(String appId, String roomId, String userId) throws Exception {
        log.info("Checking if user is kicked - appId: {}, roomId: {}, userId: {}", appId, roomId, userId);
        
        Query query = buildRoomQuery(appId, roomId);
        RoomListV2Entity room = mongoTemplate.findOne(query, RoomListV2Entity.class);
        
        if (room == null || room.getPayload() == null) {
            return false;
        }
        
        Map<String, Object> payload = room.getPayload();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> kickedUsers = (List<Map<String, Object>>) payload.get(PAYLOAD_KICKED_USERS);
        
        if (kickedUsers == null) {
            return false;
        }
        
        return kickedUsers.stream().anyMatch(user -> userId.equals(user.get(KICKED_USER_ID)));
    }
    
    @Override
    @Transactional
    public Map<String, Object> batchAddKickedUsers(String appId, String roomId, List<Map<String, String>> kickedUsers, String operator, String reason) throws Exception {
        log.info("Batch adding kicked users - appId: {}, roomId: {}, kickedUsers: {}, operator: {}, reason: {}", 
                appId, roomId, kickedUsers, operator, reason);
        
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> failedUserIds = new ArrayList<>();
        
        for (Map<String, String> userInfo : kickedUsers) {
            String userId = userInfo.get("userId");
            String userName = userInfo.get("userName");
            
            if (userId == null || userName == null) {
                failCount++;
                failedUserIds.add(userId != null ? userId : "unknown");
                continue;
            }
            
            try {
                boolean added = addKickedUser(appId, roomId, userId, userName, operator, reason);
                if (added) {
                    successCount++;
                } else {
                    failCount++;
                    failedUserIds.add(userId);
                }
            } catch (Exception e) {
                log.error("Failed to add kicked user {}: {}", userId, e.getMessage());
                failCount++;
                failedUserIds.add(userId);
            }
        }
        
        result.put("total", kickedUsers.size());
        result.put("success", successCount);
        result.put("failed", failCount);
        result.put("failedUserIds", failedUserIds);
        
        log.info("Batch add kicked users completed - total: {}, success: {}, failed: {}", 
                kickedUsers.size(), successCount, failCount);
        return result;
    }
    
    // ==================== 私有辅助方法 ====================
    
    private Query buildRoomQuery(String appId, String roomId) {
        return new Query(Criteria.where("appId").is(appId).and("roomId").is(roomId));
    }
    
    private RoomListV2Entity createRoomEntity(String appId, String roomId) {
        RoomListV2Entity room = new RoomListV2Entity();
        room.setAppId(appId);
        room.setRoomId(roomId);
        room.setCreateTime(System.currentTimeMillis());
        room.setUpdateTime(System.currentTimeMillis());
        room.setPayload(new HashMap<>());
        return mongoTemplate.save(room);
    }
}
