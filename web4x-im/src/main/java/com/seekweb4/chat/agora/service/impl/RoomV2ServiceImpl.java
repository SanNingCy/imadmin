package com.seekweb4.chat.agora.service.impl;

import com.seekweb4.chat.agora.bean.config.ChatRoomConfig;
import com.seekweb4.chat.agora.bean.dto.CreateKickOutRuleDto;
import com.seekweb4.chat.agora.bean.dto.v2.*;
import com.seekweb4.chat.agora.bean.enums.RtcChannelRulesEnum;
import com.seekweb4.chat.agora.bean.req.CreateKickOutRule;
import com.seekweb4.chat.agora.bean.req.v2.*;

import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import com.seekweb4.chat.agora.bean.enums.ReturnCodeEnum;
import com.seekweb4.chat.agora.bean.exception.BusinessException;

import com.seekweb4.chat.agora.config.WhitelistConfig;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.service.IChatRoomService;
import com.seekweb4.chat.agora.service.IRoomV2Service;
import com.seekweb4.chat.agora.service.api.ChatRoomAPIService;
import com.seekweb4.chat.agora.service.api.RtcChannelAPIService;
import com.seekweb4.chat.agora.service.api.service.AgoraRTCChannelService;
import com.seekweb4.chat.agora.utils.RedisUtil;
import io.micrometer.core.instrument.util.StringUtils;
import com.seekweb4.chat.agora.service.api.response.ChannelInfo;
import com.seekweb4.chat.agora.service.api.response.ChannelListResponse;
import com.seekweb4.chat.agora.bean.dto.UserListResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 房间V2服务实现类
 * 
 * <p>房间V2服务的具体实现，提供房间管理的完整功能。</p>
 * <p>使用Redis分布式锁保证并发安全，使用JPA操作数据库。</p>
 * 
 * @author Agora
 * @see IRoomV2Service
 * @see RoomListV2Repository
 */
@Slf4j
@Service
public class RoomV2ServiceImpl implements IRoomV2Service {
    
    /** Redis工具类，用于分布式锁和缓存操作 */
    @Resource
    private RedisUtil redisUtil;

    /** 房间列表数据仓库，用于数据库操作 */
    @Resource
    @Lazy()
    private RoomListV2Repository roomListV2Repository;
    
    @Resource
    private AgoraRTCChannelService agoraRTCChannelService;
    @Resource
    private ChatRoomAPIService chatRoomAPIService;

    /** 白名单配置，包含聊天室相关配置信息 */
    @Resource
    private WhitelistConfig whiteListConfig;

    @Resource
    private IChatRoomService iChatRoomService;
    @Resource
    private RtcChannelAPIService rtcChannelAPIService;

    // 分布式锁超时时间（秒）
    private static final long TRY_LOCK_TIMEOUT_SECOND = 10;

    // ==================== 配置注入 ====================
    
    /** 应用ID，从配置文件读取 */
    @Value("${whitelist.token.appId}")
    private String defaultAppId;
    
    /** 聊天室应用ID，从配置文件读取 */
    @Value("${whitelist.chatRoom.appId}")
    private String chatRoomAppId;
    
    /** 聊天室组织名称，从配置文件读取 */
    @Value("${whitelist.chatRoom.orgName}")
    private String chatRoomOrgName;
    
    /** 聊天室应用名称，从配置文件读取 */
    @Value("${whitelist.chatRoom.appName}")
    private String chatRoomAppName;
    
    /** 聊天室客户端ID，从配置文件读取 */
    @Value("${whitelist.chatRoom.clientId}")
    private String chatRoomClientId;
    
    /** 聊天室客户端密钥，从配置文件读取 */
    @Value("${whitelist.chatRoom.clientSecret}")
    private String chatRoomClientSecret;

    @Value("${whitelist.rtcKickOutAuth.basicAuth}")
    private String basicAuth;

    /** 应用ID，从配置文件读取 */
    @Value("${whitelist.token.appId}")
    private String appId;

    /** 应用ID，从配置文件读取 */
    @Value("${meeting.maxUser}")
    private Integer maxUser;

    /**
     * 获取分布式锁
     * 
     * <p>使用Redis实现的分布式锁，防止并发操作同一房间时的数据竞争。</p>
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @throws Exception 获取锁失败时抛出异常
     */
    public void acquireLock(String appId, String sceneId, String roomId) throws Exception {
        String lockName = getLockName(appId, sceneId, roomId);
        log.info("acquireLock, roomId:{}, lockName:{}", roomId, lockName);

        if (!redisUtil.tryLock(lockName, TRY_LOCK_TIMEOUT_SECOND)) {
            log.error("acquireLock, failed, roomId:{}, lockName:{}, TRY_LOCK_TIMEOUT_SECOND:{}", roomId, lockName,
                    TRY_LOCK_TIMEOUT_SECOND);
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.ROOM_ACQUIRE_LOCK_ERROR);
        }
    }

    /**
     * 添加房间到列表
     * 
     * <p>将房间信息插入到数据库中。</p>
     * 
     * @param addRoomReq 添加房间请求参数
     */
    @Override
    public void addRoomList(AddRoomReq addRoomReq) {
        RoomListV2Entity roomListEntity = new RoomListV2Entity()
                .setId(addRoomReq.getId())
                .setAppId(addRoomReq.getAppId())
                .setSceneId(addRoomReq.getSceneId())
                .setRoomId(addRoomReq.getRoomId())
                .setPayload(addRoomReq.getPayload())
                .setGroupId(addRoomReq.getGroupId())
                .setOwnerId(addRoomReq.getOwnerId())
                .setLastActiveTime(System.currentTimeMillis())
                .setStatus("pending_create")
                // .setStatus("active")
                .setUpdateTime(System.currentTimeMillis())
                .setCreateTime(System.currentTimeMillis());
        // 插入数据
        roomListV2Repository.insert(roomListEntity);
    }

    /**
     * 更新房间信息
     * 
     * <p>更新指定房间的负载数据和更新时间。</p>
     * <p>使用分布式锁保证操作的原子性。</p>
     * 
     * @param roomUpdateReq 房间更新请求参数
     * @throws Exception 房间不存在或锁操作失败时抛出异常
     */
    @Override
    public void update(RoomUpdateReq roomUpdateReq) throws Exception {
        acquireLock(roomUpdateReq.getAppId(), roomUpdateReq.getSceneId(), roomUpdateReq.getRoomId());
        String id = getId(roomUpdateReq.getAppId(), roomUpdateReq.getSceneId(), roomUpdateReq.getRoomId());
        Optional<RoomListV2Entity> opt = roomListV2Repository.findById(id);
        if (!opt.isPresent()) {
            releaseLock(roomUpdateReq.getAppId(), roomUpdateReq.getSceneId(), roomUpdateReq.getRoomId());
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.ROOM_NOT_EXISTS_ERROR);
        }

        RoomListV2Entity roomListEntity = opt.get();
        roomListEntity.setPayload(roomUpdateReq.getPayload());
        roomListEntity.setUpdateTime(System.currentTimeMillis());
        roomListV2Repository.save(roomListEntity);
        releaseLock(roomUpdateReq.getAppId(), roomUpdateReq.getSceneId(), roomUpdateReq.getRoomId());
        log.info("update, success, roomUpdateReq:{}", roomUpdateReq);
    }

    /**
     * 生成房间唯一ID
     * 
     * <p>使用MD5哈希算法生成房间的唯一标识。</p>
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @return MD5哈希值作为唯一ID
     */
    public String getId(String appId, String sceneId, String roomId) {
        String key = appId + sceneId + roomId;
        return DigestUtils.md5DigestAsHex((key).getBytes());
    }

    /**
     * 创建房间
     * 
     * <p>创建新房间，如果房间已存在则返回现有房间信息。</p>
     * <p>使用分布式锁保证创建操作的原子性。</p>
     * 
     * @param roomCreateReq 房间创建请求参数
     * @return 房间创建结果
     * @throws Exception 锁操作失败时抛出异常
     */
    @Override
    public RoomCreateDto create(RoomCreateReq roomCreateReq) throws Exception {
        // 1. 设置默认appId和sceneId（如果为空）
        String appId = defaultAppId;
        String sceneId = StringUtils.isBlank(roomCreateReq.getSceneId()) ? "live_streaming" : roomCreateReq.getSceneId();
//        String sceneId = StringUtils.isBlank(roomCreateReq.getSceneId()) ? "live_streaming" : roomCreateReq.getSceneId();
        // TODO 使用已有的会议ID
        String roomId = roomCreateReq.getRoomId();

        acquireLock(appId, sceneId, roomId);
        //acquireLock(roomCreateReq.getAppId(), roomCreateReq.getSceneId(), actualRoomId);

        try {
            // 3. 根据会议ID查询群ID和房间信息
            // TODO 根据我生成的会议ID去查询群ID
            List<RoomListV2Entity> rooms = roomListV2Repository.findByRoomIdOrderByUpdateTimeDesc(roomId);
//            Optional<RoomListV2Entity> opt = roomListV2Repository.findByRoomId(actualRoomId);

            // 4. 如果房间已存在，返回现有房间信息
            if (rooms != null && !rooms.isEmpty()) {
                RoomListV2Entity existingRoom = rooms.get(0);
                // 更新房间信息，确保保持原有的MongoDB _id
                String originalId = existingRoom.getId();
                
                // 获取前端传过来的payload
                Map<String, Object> payload = roomCreateReq.getPayload();
                if (payload == null) {
                    payload = new HashMap<>();
                }
                
                // 添加allMic、allMute和maxUsers字段
                Boolean reqAllMic = roomCreateReq.getAllMic();
                Boolean reqAllMute = roomCreateReq.getAllMute();
                payload.put("allMic", reqAllMic != null ? reqAllMic : true);
                payload.put("allMute", reqAllMute != null ? reqAllMute : false);
                payload.put("maxUsers", maxUser);
                
                existingRoom.setPayload(payload);
                existingRoom.setUpdateTime(System.currentTimeMillis());
                existingRoom.setStatus("active");
                existingRoom.setSceneId(sceneId);
                existingRoom.setId(originalId); // 确保MongoDB _id不变
                
                // 调用save更新接口去更新数据
                roomListV2Repository.save(existingRoom);
                
                log.info("create, room exists, updated, roomId:{}", roomId);
//                log.info("create, room exists, updated, roomId:{}", actualRoomId);
                return new RoomCreateDto()
                        .setAppId(existingRoom.getAppId())
                        .setSceneId(existingRoom.getSceneId())
                        .setRoomId(existingRoom.getRoomId())
                        .setPayload(existingRoom.getPayload())
                        .setUpdateTime(existingRoom.getUpdateTime())
                        .setCreateTime(existingRoom.getCreateTime())
                        .setGroupId(existingRoom.getGroupId())
                        .setStatus(existingRoom.getStatus());
            }
            
            long currentTime = System.currentTimeMillis();
            
            // 获取前端传过来的payload并添加必要字段
            Map<String, Object> payload = roomCreateReq.getPayload();
            if (payload == null) {
                payload = new HashMap<>();
            }
            
            // 添加allMic、allMute和maxUsers字段
            Boolean reqAllMic = roomCreateReq.getAllMic();
            Boolean reqAllMute = roomCreateReq.getAllMute();
            payload.put("allMic", reqAllMic != null ? reqAllMic : true);
            payload.put("allMute", reqAllMute != null ? reqAllMute : false);
            payload.put("maxUsers", maxUser);
            
            // 创建新房间实体并保存到数据库
            String id = getId(appId, sceneId, roomId);
            RoomListV2Entity newRoom = new RoomListV2Entity()
                    .setId(id)
                    .setAppId(appId)
                    .setSceneId(sceneId)
                    .setRoomId(roomId)
                    .setPayload(payload)
                    .setUpdateTime(currentTime)
                    .setCreateTime(currentTime)
                    .setGroupId(roomCreateReq.getGroupId())
                    .setStatus("active");
            roomListV2Repository.save(newRoom);
            
            log.info("create, success, roomCreateReq:{}, actualRoomId:{}", roomCreateReq, roomId);
            return new RoomCreateDto()
                    .setAppId(appId)
                    .setSceneId(sceneId)
                    .setRoomId(roomId)
//                    .setRoomId(actualRoomId)
                    .setPayload(payload)
                    .setUpdateTime(currentTime)
                    .setCreateTime(currentTime)
                    .setGroupId(roomCreateReq.getGroupId())
                    .setStatus("active");
                    
        } finally {
            releaseLock(appId, sceneId, roomId);
        }
    }

    /**
     * 简化版创建会议室
     * 
     * <p>前端只需要传递群ID和群主ID，其他配置由后端自动生成。</p>
     * <p>包括：appId、sceneId、roomId、payload、聊天室配置、IM配置等。</p>
     * 
     * @param simpleRoomCreateReq 简化版会议室创建请求参数
     * @return 会议室创建结果，包含所有自动生成的配置信息
     * @throws Exception 创建过程中的异常
     */
    @Override
    public SimpleRoomCreateDto createSimpleRoom(SimpleRoomCreateReq simpleRoomCreateReq) throws Exception {
        log.info("createSimpleRoom, request: {}", simpleRoomCreateReq);
        
        // 1. 自动生成配置
        String appId = defaultAppId;
        String sceneId = generateSceneId(simpleRoomCreateReq.getType());
        String roomId = generateRoomId(simpleRoomCreateReq.getGroupId());
        
        // 2. 生成会议室名称
        String roomName = generateRoomName(simpleRoomCreateReq.getRoomName(), simpleRoomCreateReq.getGroupId());
        
        // 3. 计算最大人数（默认 maxUser）并构建payload
        int effectiveMaxUsers = simpleRoomCreateReq.getMaxUsers() == null ? maxUser : simpleRoomCreateReq.getMaxUsers();
        Map<String, Object> payload = buildSimpleRoomPayload(simpleRoomCreateReq, roomName, effectiveMaxUsers);
        
        // 4. 检查房间是否已存在
        acquireLock(appId, sceneId, roomId);
        String id = getId(appId, sceneId, roomId);
        Optional<RoomListV2Entity> existingRoom = roomListV2Repository.findById(id);
        
        if (existingRoom.isPresent()) {
            // 房间已存在，返回现有房间信息
            releaseLock(appId, sceneId, roomId);
            log.info("createSimpleRoom, room already exists, roomId: {}", roomId);
            return buildSimpleRoomCreateDto(existingRoom.get(), simpleRoomCreateReq);
        }
        
        // 5. 创建新房间
        long currentTime = System.currentTimeMillis();
        addRoomList(new AddRoomReq()
                .setId(id)
                .setAppId(appId)
                .setSceneId(sceneId)
                .setRoomId(roomId)
                .setPayload(payload)
                .setGroupId(simpleRoomCreateReq.getGroupId())
                .setOwnerId(simpleRoomCreateReq.getOwnerId())
                .setUpdateTime(currentTime)
                .setCreateTime(currentTime));
        
        // 6. 更新房间状态为待创建
        // updateRoomStatus(appId, sceneId, roomId, "pending_create");
        
        releaseLock(appId, sceneId, roomId);
        
        // 7. 构建响应（过滤敏感信息）
        SimpleRoomCreateDto result = new SimpleRoomCreateDto()
                .setAppId(appId) // 保留appId
                .setSceneId(sceneId)
                .setRoomId(roomId)
                .setGroupId(simpleRoomCreateReq.getGroupId())
                .setOwnerId(simpleRoomCreateReq.getOwnerId())
                .setStatus("pending_create")
                .setPayload(filterSensitivePayload(payload)) // 过滤敏感信息
                .setCreateTime(currentTime)
                .setUpdateTime(currentTime)
                .setChatRoomConfig(buildChatRoomConfig(roomName, effectiveMaxUsers))
                .setImConfig(null); // 不暴露imConfig
        
        log.info("createSimpleRoom, success, roomId: {}, groupId: {}", roomId, simpleRoomCreateReq.getGroupId());
        return result;
    }

    /**
     * 销毁房间
     * 
     * <p>从房间列表中移除指定房间。</p>
     * 
     * @param roomDestroyReq 房间销毁请求参数
     * @throws Exception 操作失败时抛出异常
     */
    @Override
    public void destroy(RoomDestroyReq roomDestroyReq) throws Exception {
        removeRoomList(roomDestroyReq);
        log.info("destroy, success, roomDestroyReq:{}", roomDestroyReq);
    }

    /**
     * 生成锁名称
     * 
     * <p>使用MD5哈希算法生成分布式锁的名称。</p>
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @return MD5哈希值作为锁名称
     */
    public String getLockName(String appId, String sceneId, String roomId) {
        String lockName = appId + sceneId + roomId;
        return DigestUtils.md5DigestAsHex((lockName).getBytes());
    }

    /**
     * 获取房间列表
     * 
     * <p>分页查询房间列表，按创建时间降序排列。</p>
     * 
     * @param roomListReq 房间列表查询请求参数
     * @return 房间列表查询结果
     */
    @Override
    public RoomListDto<RoomListEntity> getRoomList(RoomListReq roomListReq) {
        log.info("getRoomList, roomListReq:{}", roomListReq);

        Pageable pageable = PageRequest.of(0, roomListReq.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<RoomListV2Entity> roomList = roomListV2Repository.findByCreateTimeLessThanAndAppIdAndSceneId(
                roomListReq.getLastCreateTime(),
                roomListReq.getAppId(),
                roomListReq.getSceneId(),
                pageable);

        // 使用流转换为RoomListDto
        return new RoomListDto<RoomListEntity>()
                .setCount(roomList.getSize())
                .setPageSize(roomListReq.getPageSize())
                .setList(roomList.getContent().stream().map(roomListEntity -> new RoomListEntity()
                        .setAppId(roomListEntity.getAppId())
                        .setSceneId(roomListEntity.getSceneId())
                        .setRoomId(roomListEntity.getRoomId())
                        .setPayload(roomListEntity.getPayload())
                        .setUpdateTime(roomListEntity.getUpdateTime())
                        .setCreateTime(roomListEntity.getCreateTime())
                ).collect(Collectors.toList()));
    }

    /**
     * 查询房间详情
     * 
     * <p>根据房间唯一标识查询房间的详细信息。</p>
     * 
     * @param roomQueryReq 房间查询请求参数
     * @return 房间详情信息
     * @throws Exception 房间不存在时抛出异常
     */
    @Override
    public RoomQueryDto query(RoomQueryReq roomQueryReq) throws Exception {
        Optional<RoomListV2Entity> opt = roomListV2Repository.findById(getId(roomQueryReq.getAppId(), roomQueryReq.getSceneId(), roomQueryReq.getRoomId()));
        if (!opt.isPresent()) {
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.ROOM_NOT_EXISTS_ERROR);
        }
        RoomListV2Entity roomListEntity = opt.get();
        return new RoomQueryDto()
                .setAppId(roomListEntity.getAppId())
                .setSceneId(roomListEntity.getSceneId())
                .setCreateTime(roomListEntity.getCreateTime())
                .setUpdateTime(roomListEntity.getUpdateTime())
                .setRoomId(roomQueryReq.getRoomId())
                .setPayload(roomListEntity.getPayload());
    }

    /**
     * 释放分布式锁
     * 
     * <p>释放之前获取的Redis分布式锁。</p>
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @throws Exception 释放锁失败时抛出异常
     */
    public void releaseLock(String appId, String sceneId, String roomId) throws Exception {
        String lockName = getLockName(appId, sceneId, roomId);
        log.info("releaseLock, roomId:{}, lockName:{}", roomId, lockName);

        if (!redisUtil.unlock(lockName)) {
            log.error("releaseLock, failed, roomId:{}, lockName:{}", roomId, lockName);
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.ROOM_RELEASE_LOCK_ERROR);
        }
    }

    /**
     * 从列表中移除房间
     * 
     * <p>使用分布式锁保证删除操作的原子性。</p>
     * 
     * @param roomDestroyReq 房间销毁请求参数
     * @throws Exception 锁操作失败时抛出异常
     */
    @Override
    public void removeRoomList(RoomDestroyReq roomDestroyReq) throws Exception {
        acquireLock(appId, roomDestroyReq.getSceneId(), roomDestroyReq.getRoomId());
        Optional<RoomListV2Entity> byRoomId = roomListV2Repository.findByRoomId(roomDestroyReq.getRoomId());
        if (!byRoomId.isPresent()) {
            return;
        }
        try {
            RoomListV2Entity roomListV2Entity = byRoomId.get();
            roomListV2Entity.setStatus("destroyed");
            roomListV2Repository.save(roomListV2Entity);
            //通知用户发起会议室踢出所有人并封禁会议操作
            roomListV2Entity.getOwnerId();
            //封禁会议，并踢出所有用户，禁止发布语音和视频；
            List<String> privileges = Arrays.asList(RtcChannelRulesEnum.JOIN_CHANNEL.getRule(), RtcChannelRulesEnum.PUBLISH_AUDIO.getRule(), RtcChannelRulesEnum.PUBLISH_VIDEO.getRule());
            CreateKickOutRule rule = new CreateKickOutRule()
                    .setAppId(appId)
                    .setCname(roomListV2Entity.getRoomId())
                    .setPrivileges(privileges);

            ChatRoomConfig chatRoomFromWhitelist = whiteListConfig.getChatRoomFromWhitelist(appId);
            String appToken = iChatRoomService.getAppToken(chatRoomFromWhitelist.getOrgName(), chatRoomFromWhitelist.getAppName(), chatRoomFromWhitelist.getClientId(), chatRoomFromWhitelist.getClientSecret());
            appToken = "Bearer".concat(" ").concat(appToken);
            // 会议销毁，调用API创建踢出规则，禁止加入、禁止语聊、禁止视频并踢出所有人（软删除）
            CreateKickOutRuleDto kickOutRuleDTO = rtcChannelAPIService.createKickOutRule(rule, basicAuth);
            if (kickOutRuleDTO == null) {
                throw new Exception("failed to kick out user");
            }
            if (roomListV2Entity.getChatRoomId() == null) {
                return;
            }
            //调用声网API销毁聊天室
            chatRoomAPIService.deleteChatRoom(chatRoomFromWhitelist.getOrgName(),chatRoomFromWhitelist.getAppName() ,appToken,roomListV2Entity.getChatRoomId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseLock(appId, roomDestroyReq.getSceneId(), roomDestroyReq.getRoomId());
        }
        log.info("removeRoomList, roomDestroyReq:{}", roomDestroyReq);
    }

    // ==================== 房间状态管理方法 ====================
    /**
     * 更新房间状态
     * 
     * <p>更新指定房间的状态，支持状态转换验证。</p>
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @param status 新状态 (active/inactive/destroyed)
     * @throws Exception 更新过程中的异常
     */
    public void updateRoomStatus(String appId, String sceneId, String roomId, String status) throws Exception {
        log.info("updateRoomStatus, appId:{}, sceneId:{}, roomId:{}, status:{}", appId, sceneId, roomId, status);
        
        // 验证状态合法性
        if (!isValidStatus(status)) {
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.PARAMS_ERROR);
        }
        
        acquireLock(appId, sceneId, roomId);
        String id = getId(appId, sceneId, roomId);
        Optional<RoomListV2Entity> opt = roomListV2Repository.findById(id);
        
        if (!opt.isPresent()) {
            releaseLock(appId, sceneId, roomId);
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.ROOM_NOT_EXISTS_ERROR);
        }
        
        RoomListV2Entity roomListEntity = opt.get();
        String oldStatus = roomListEntity.getStatus();
        
        // 验证状态转换合法性
        if (!isValidStatusTransition(oldStatus, status)) {
            releaseLock(appId, sceneId, roomId);
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.PARAMS_ERROR);
        }
        
        roomListEntity.setStatus(status);
        roomListEntity.setUpdateTime(System.currentTimeMillis());
        roomListV2Repository.save(roomListEntity);
        releaseLock(appId, sceneId, roomId);
        
        log.info("updateRoomStatus success, appId:{}, roomId:{}, oldStatus:{}, newStatus:{}", 
                appId, roomId, oldStatus, status);
    }

    /**
     * 用户加入房间时更新状态为活跃
     * 
     * <p>当有用户加入房间时，将房间状态更新为active。</p>
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @throws Exception 更新过程中的异常
     */
    public void userJoinRoom(String appId, String sceneId, String roomId) throws Exception {
        log.info("userJoinRoom, appId:{}, sceneId:{}, roomId:{}", appId, sceneId, roomId);
        updateRoomStatus(appId, sceneId, roomId, "active");
    }

    /**
     * 用户离开房间时更新状态
     * 
     * <p>当用户离开房间时，将房间状态更新为inactive。</p>
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @throws Exception 更新过程中的异常
     */
    public void userLeaveRoom(String appId, String sceneId, String roomId) throws Exception {
        log.info("userLeaveRoom, appId:{}, sceneId:{}, roomId:{}", appId, sceneId, roomId);
        updateRoomStatus(appId, sceneId, roomId, "inactive");
    }

    /**
     * 获取房间当前状态
     * 
     * <p>查询指定房间的当前状态。</p>
     * 
     * @param appId 应用ID
     * @param sceneId 场景ID
     * @param roomId 房间ID
     * @return 房间当前状态
     * @throws Exception 查询过程中的异常
     */
    public String getRoomStatus(String appId, String sceneId, String roomId) throws Exception {
        log.info("getRoomStatus, appId:{}, sceneId:{}, roomId:{}", appId, sceneId, roomId);

        String id = getId(appId, sceneId, roomId);
        Optional<RoomListV2Entity> opt = roomListV2Repository.findById(id);
        
        if (!opt.isPresent()) {
            throw new BusinessException(HttpStatus.OK.value(), ReturnCodeEnum.ROOM_NOT_EXISTS_ERROR);
        }
        
        String status = opt.get().getStatus();
        log.info("getRoomStatus result, appId:{}, roomId:{}, status:{}", appId, roomId, status);
        return status;
    }

    /**
     * 验证状态是否合法
     * 
     * @param status 要验证的状态
     * @return true表示合法，false表示不合法
     */
    private boolean isValidStatus(String status) {
        return "pending_create".equals(status) || "active".equals(status) || "inactive".equals(status) || "destroyed".equals(status);
    }

    /**
     * 验证状态转换是否合法
     * 
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @return true表示转换合法，false表示转换不合法
     */
    private boolean isValidStatusTransition(String oldStatus, String newStatus) {
        if (oldStatus == null || newStatus == null) {
            return false;
        }
        
        // 相同状态转换总是合法的
        if (oldStatus.equals(newStatus)) {
            return true;
        }
        
        // 已销毁的房间不能转换到其他状态
        if ("destroyed".equals(oldStatus)) {
            return false;
        }
        
        // 任何状态都可以转换为destroyed
        if ("destroyed".equals(newStatus)) {
            return true;
        }
        
        // 其他状态转换规则
        switch (oldStatus) {
            case "pending_create":
                return "active".equals(newStatus) || "inactive".equals(newStatus) || "destroyed".equals(newStatus);
            case "inactive":
                return "active".equals(newStatus) || "destroyed".equals(newStatus);
            case "active":
                return "inactive".equals(newStatus) || "destroyed".equals(newStatus);
            default:
                return false;
        }
    }

    /**
     * 查询群内正在进行的会议
     * 
     * <p>根据群ID查询该群内所有活跃状态的会议。</p>
     * <p>同时查询数据库和声网API，确保能获取到所有正在进行的会议。</p>
     * 
     * @param appId 应用ID
     * @param groupId 群ID
     * @param sceneId 场景ID（可选）
     * @return 群会议信息
     * @throws Exception 查询过程中的异常
     */
    @Override
    public GroupMeetingResponseDto getGroupActiveMeetings(String appId, String groupId, String sceneId) throws Exception {
        log.info("getGroupActiveMeetings, appId:{}, groupId:{}, sceneId:{}", appId, groupId, sceneId);
        
        List<GroupMeetingInfoDto> meetingInfoList = new ArrayList<>();
        
        // 1. 查询数据库中的活跃会议
        List<RoomListV2Entity> dbActiveMeetings = getActiveMeetingsFromDatabase(appId, groupId, sceneId);
        
        // 2. 查询声网API中的活跃频道
        List<GroupMeetingInfoDto> agoraActiveMeetings = getActiveMeetingsFromAgoraAPI(appId, groupId);
        
        // 3. 合并数据库和声网API的结果
        Map<String, GroupMeetingInfoDto> mergedMeetings = new HashMap<>();
        
        // 添加数据库中的会议
        for (RoomListV2Entity entity : dbActiveMeetings) {
            GroupMeetingInfoDto dto = new GroupMeetingInfoDto()
                    .setAppId(null) // 不暴露appId
                    .setSceneId(entity.getSceneId())
                    .setRoomId(entity.getRoomId())
                    .setGroupId(entity.getGroupId())
                    .setStatus(entity.getStatus())
                    .setPayload(filterSensitivePayload(entity.getPayload()))
                    .setCreateTime(entity.getCreateTime())
                    .setUpdateTime(entity.getUpdateTime())
                    .setLastActiveTime(entity.getLastActiveTime())
                    .setOwnerId(entity.getOwnerId())
                    .setChatRoomId(entity.getChatRoomId());
            mergedMeetings.put(entity.getRoomId(), dto);
        }
        
        // 添加声网API中的会议（如果数据库中没有记录）
        for (GroupMeetingInfoDto agoraDto : agoraActiveMeetings) {
            if (!mergedMeetings.containsKey(agoraDto.getRoomId())) {
                mergedMeetings.put(agoraDto.getRoomId(), agoraDto);
            }
        }
        
        meetingInfoList = new ArrayList<>(mergedMeetings.values());
        // 合并后的会议转为列表，并再次保证只返回active状态
//        meetingInfoList = mergedMeetings.values().stream()
//                .filter(dto -> "active".equals(dto.getStatus()))
//                .collect(java.util.stream.Collectors.toList());

        
        GroupMeetingResponseDto response = new GroupMeetingResponseDto()
                .setGroupId(groupId)
                .setHasActiveMeeting(!meetingInfoList.isEmpty())
                .setActiveMeetingCount(meetingInfoList.size())
                .setActiveMeetings(meetingInfoList);
        
        log.info("getGroupActiveMeetings result, groupId:{}, activeMeetingCount:{}, dbCount:{}, agoraCount:{}", 
                groupId, meetingInfoList.size(), dbActiveMeetings.size(), agoraActiveMeetings.size());
        
        return response;
    }
    
    /**
     * 从数据库查询活跃会议
     */
    private List<RoomListV2Entity> getActiveMeetingsFromDatabase(String appId, String groupId, String sceneId) {
        List<RoomListV2Entity> allMeetings;
        
        if (appId != null && !appId.isEmpty()) {
            if (sceneId != null && !sceneId.isEmpty()) {
                allMeetings = roomListV2Repository.findByAppIdAndGroupIdAndSceneId(appId, groupId, sceneId);
            } else {
                allMeetings = roomListV2Repository.findByAppIdAndGroupId(appId, groupId);
            }
        } else {
            if (sceneId != null && !sceneId.isEmpty()) {
                allMeetings = roomListV2Repository.findByGroupIdAndSceneId(groupId, sceneId);
            } else {
                allMeetings = roomListV2Repository.findByGroupId(groupId);
            }
        }
        
        // 处理重复数据：按roomId分组，每个roomId只保留最新的一条记录
        Map<String, RoomListV2Entity> latestRoomsMap = new HashMap<>();
        for (RoomListV2Entity room : allMeetings) {
            String roomId = room.getRoomId();
            RoomListV2Entity existingRoom = latestRoomsMap.get(roomId);
            if (existingRoom == null || room.getUpdateTime() > existingRoom.getUpdateTime()) {
                latestRoomsMap.put(roomId, room);
            }
        }
        
        // 过滤出活跃状态的会议
        return latestRoomsMap.values().stream()
                .filter(room -> "active".equals(room.getStatus()))
                .collect(Collectors.toList());
    }
    
    /**
     * 从声网API查询活跃频道
     */
    private List<GroupMeetingInfoDto> getActiveMeetingsFromAgoraAPI(String appId, String groupId) {
        List<GroupMeetingInfoDto> agoraActiveMeetings = new ArrayList<>();
        
        try {
            // 使用默认appId如果没有指定
            String effectiveAppId = (appId != null && !appId.isEmpty()) ? appId : defaultAppId;
            
            // 获取所有频道列表
            ChannelListResponse channelListResponse = agoraRTCChannelService.getAllChannels(effectiveAppId, 0, 500);
            
            if (channelListResponse != null && channelListResponse.getData() != null && 
                channelListResponse.getData().getChannels() != null) {
                
                for (ChannelInfo channelInfo : channelListResponse.getData().getChannels()) {
                    String channelName = channelInfo.getChannelName();
                    
                    // 检查频道名是否包含群ID（移动端创建的频道通常包含群ID）
                    if (channelName != null && channelName.contains(groupId) && channelInfo.getUserCount() > 0) {
                        // 获取频道详细信息确认是否活跃
                        UserListResponseDto channelDetail = agoraRTCChannelService.getChannelInfo(effectiveAppId, channelName);
                        
                        if (channelDetail != null && channelDetail.getData() != null && 
                            Boolean.TRUE.equals(channelDetail.getData().getChannelExist()) && 
                            channelDetail.getData().getTotal() > 0) {
                            
                            // 创建会议信息DTO
                            GroupMeetingInfoDto dto = new GroupMeetingInfoDto()
                                    .setAppId(null) // 不暴露appId
                                    .setSceneId("live_streaming") // 默认场景
                                    .setRoomId(channelName)
                                    .setGroupId(groupId)
                                    .setStatus("active")
                                    .setCreateTime(System.currentTimeMillis())
                                    .setUpdateTime(System.currentTimeMillis())
                                    .setLastActiveTime(System.currentTimeMillis());
                            
                            // 构建payload信息
                            Map<String, Object> payload = new HashMap<>();
                            payload.put("roomName", "群会议");
                            payload.put("userCount", channelDetail.getData().getTotal());
                            payload.put("mode", channelDetail.getData().getMode());
                            dto.setPayload(payload);
                            
                            agoraActiveMeetings.add(dto);
                        }
                    }
                }
            }
            
            log.info("从声网API查询到活跃频道数量: {}, groupId: {}", agoraActiveMeetings.size(), groupId);
            
        } catch (Exception e) {
            log.warn("查询声网API频道信息失败, groupId: {}, error: {}", groupId, e.getMessage());
            // 不抛出异常，继续使用数据库结果
        }
        
        return agoraActiveMeetings;
    }

    /**
     * 从payload中获取type字段
     * 
     * @param payload 负载数据
     * @return type值，如果不存在则返回null
     */
    private Integer getTypeFromPayload(Map<String, Object> payload) {
        if (payload == null) {
            return null;
        }
        Object typeObj = payload.get("type");
        if (typeObj instanceof Integer) {
            return (Integer) typeObj;
        }
        return null;
    }

    /**
     * 根据type生成sceneId
     * 
     * @param type 房间类型
     * @return 场景ID
     */
    private String generateSceneId(Integer type) {
        if (type == null) {
            return "live_streaming"; // 默认场景
        }
        
        switch (type) {
            case 0:
                return "live_streaming"; // 直播场景
            case 1:
                return "voice_chat"; // 语音聊天
            case 2:
                return "video_call"; // 视频通话
            case 3:
                return "conference"; // 会议
            default:
                return "live_streaming"; // 默认场景
        }
    }

    /**
     * 生成房间ID
     * 
     * @param groupId 群ID（可选）
     * @return 房间ID
     */
    private String generateRoomId(String groupId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 10000));
        
        if (groupId != null && !groupId.isEmpty()) {
            return "group_" + groupId + "_room_" + timestamp + "_" + random;
        } else {
            return "room_" + timestamp + "_" + random;
        }
    }

    // ==================== 简化版会议室创建辅助方法 ====================

    /**
     * 生成会议室名称
     * 
     * @param customName 自定义名称（可选）
     * @param groupId 群ID
     * @return 会议室名称
     */
    private String generateRoomName(String customName, String groupId) {
        if (customName != null && !customName.trim().isEmpty()) {
            return customName.trim();
        }
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        if (groupId != null && !groupId.isEmpty()) {
            return "群会议_" + groupId + "_" + timestamp;
        } else {
            return "会议室_" + timestamp;
        }
    }

    /**
     * 构建简化版会议室payload
     * 
     * @param request 创建请求
     * @param roomName 会议室名称
     * @return payload数据
     */
    private Map<String, Object> buildSimpleRoomPayload(SimpleRoomCreateReq request, String roomName, int effectiveMaxUsers) {
        Map<String, Object> payload = new java.util.HashMap<>();
        
        // 基本信息
        payload.put("roomName", roomName);
        payload.put("description", "群聊会议室");
        payload.put("maxUsers", effectiveMaxUsers);
        payload.put("type", request.getType());
        
        // 用户信息
        Map<String, Object> user = new java.util.HashMap<>();
        user.put("username", request.getOwnerId());
        payload.put("user", user);
        
        // 会议室默认设置（前端传了就用前端的，未传则使用默认值）
        Boolean reqAllMic = request.getAllMic();
        Boolean reqAllMute = request.getAllMute();
        payload.put("allMic", reqAllMic != null ? reqAllMic : true);
        payload.put("allMute", reqAllMute != null ? reqAllMute : false);
        
        // 聊天室配置
        Map<String, Object> chatRoomConfig = new java.util.HashMap<>();
        chatRoomConfig.put("maxUsers", effectiveMaxUsers);
        chatRoomConfig.put("name", roomName);
        payload.put("chatRoomConfig", chatRoomConfig);
        
        // IM配置
        Map<String, Object> imConfig = new java.util.HashMap<>();
        imConfig.put("appName", chatRoomAppName);
        imConfig.put("clientId", chatRoomClientId);
        imConfig.put("clientSecret", chatRoomClientSecret);
        imConfig.put("orgName", chatRoomOrgName);
        payload.put("imConfig", imConfig);
        
        return payload;
    }

    /**
     * 构建聊天室配置
     * 
     * @param roomName 会议室名称
     * @param maxUsers 最大用户数
     * @return 聊天室配置
     */
    private SimpleRoomCreateDto.ChatRoomConfigDto buildChatRoomConfig(String roomName, Integer maxUsers) {
        return new SimpleRoomCreateDto.ChatRoomConfigDto()
                .setMaxUsers(maxUsers)
                .setName(roomName);
    }

    /**
     * 构建IM配置
     * 
     * @return IM配置
     */
    private SimpleRoomCreateDto.ImConfigDto buildImConfig() {
        return new SimpleRoomCreateDto.ImConfigDto()
                .setAppName(chatRoomAppName)
                .setClientId(chatRoomClientId)
                .setClientSecret(chatRoomClientSecret)
                .setOrgName(chatRoomOrgName);
    }

    /**
     * 构建简化版会议室创建响应（从现有房间）
     * 
     * @param existingRoom 现有房间实体
     * @param request 创建请求
     * @return 响应数据
     */
    private SimpleRoomCreateDto buildSimpleRoomCreateDto(RoomListV2Entity existingRoom, SimpleRoomCreateReq request) {
        return new SimpleRoomCreateDto()
                .setAppId(existingRoom.getAppId()) // 保留appId
                .setSceneId(existingRoom.getSceneId())
                .setRoomId(existingRoom.getRoomId())
                .setGroupId(existingRoom.getGroupId())
                .setOwnerId(request.getOwnerId())
                .setStatus(existingRoom.getStatus())
                .setPayload(filterSensitivePayload(existingRoom.getPayload())) // 过滤敏感信息
                .setCreateTime(existingRoom.getCreateTime())
                .setUpdateTime(existingRoom.getUpdateTime())
                .setChatRoomConfig(extractChatRoomConfig(existingRoom.getPayload()))
                .setImConfig(null); // 不暴露imConfig
    }

    /**
     * 从payload中提取聊天室配置
     * 
     * @param payload 负载数据
     * @return 聊天室配置
     */
    private SimpleRoomCreateDto.ChatRoomConfigDto extractChatRoomConfig(Map<String, Object> payload) {
        if (payload == null) {
            return buildChatRoomConfig("默认会议室", 1000);
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> chatRoomConfig = (Map<String, Object>) payload.get("chatRoomConfig");
        if (chatRoomConfig == null) {
            return buildChatRoomConfig("默认会议室", 1000);
        }
        
        return new SimpleRoomCreateDto.ChatRoomConfigDto()
                .setMaxUsers((Integer) chatRoomConfig.getOrDefault("maxUsers", 1000))
                .setName((String) chatRoomConfig.getOrDefault("name", "默认会议室"));
    }

    /**
     * 从payload中提取IM配置
     * 
     * @param payload 负载数据
     * @return IM配置
     */
    private SimpleRoomCreateDto.ImConfigDto extractImConfig(Map<String, Object> payload) {
        if (payload == null) {
            return buildImConfig();
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> imConfig = (Map<String, Object>) payload.get("imConfig");
        if (imConfig == null) {
            return buildImConfig();
        }
        
        return new SimpleRoomCreateDto.ImConfigDto()
                .setAppName((String) imConfig.getOrDefault("appName", chatRoomAppName))
                .setClientId((String) imConfig.getOrDefault("clientId", chatRoomClientId))
                .setClientSecret((String) imConfig.getOrDefault("clientSecret", chatRoomClientSecret))
                .setOrgName((String) imConfig.getOrDefault("orgName", chatRoomOrgName));
    }

    /**
     * 过滤payload中的敏感信息
     * 
     * @param payload 原始payload
     * @return 过滤后的payload
     */
    private Map<String, Object> filterSensitivePayload(Map<String, Object> payload) {
        if (payload == null) {
            return null;
        }
        
        Map<String, Object> filteredPayload = new java.util.HashMap<>(payload);
        
        // 完全移除imConfig字段
        filteredPayload.remove("imConfig");
        
        return filteredPayload;
    }
    
    @Override
    public RoomDetailDto getRoomDetail(String roomId) throws Exception {
        log.info("getRoomDetail, roomId: {}", roomId);
        
        if (StringUtils.isBlank(roomId)) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }
        
        // Optional<RoomListV2Entity> roomOpt = roomListV2Repository.findByRoomId(roomId);
        // if (!roomOpt.isPresent()) {
        // 查询会议室（兼容历史重复数据：按更新时间倒序取最新一条）
        List<RoomListV2Entity> rooms = roomListV2Repository.findByRoomIdOrderByUpdateTimeDesc(roomId);
        if (rooms == null || rooms.isEmpty()) {
            throw new RuntimeException("会议室不存在");
        }
        //RoomListV2Entity room = roomOpt.get();
        RoomListV2Entity room = rooms.get(0);
        
        // 构建响应
        RoomDetailDto result = new RoomDetailDto()
                .setAppId(room.getAppId())
                .setSceneId(room.getSceneId())
                .setRoomId(room.getRoomId())
                .setGroupId(room.getGroupId())
                .setOwnerId(room.getOwnerId())
                .setStatus(room.getStatus())
                .setPayload(filterSensitivePayload(room.getPayload())) // 过滤敏感信息
                .setCreateTime(room.getCreateTime())
                .setUpdateTime(room.getUpdateTime())
                .setLastActiveTime(room.getLastActiveTime())
                .setChatRoomConfig(extractRoomDetailChatRoomConfig(room.getPayload()))
                .setImConfig(null) // 不暴露imConfig
                .setChatRoomId(room.getChatRoomId());
        
        log.info("getRoomDetail, success, roomId: {}", roomId);
        return result;
    }
    
    @Override
    public boolean updateRoomSettings(RoomSettingsUpdateReq request) throws Exception {
        log.info("updateRoomSettings, roomId: {}, allMic: {}, allMute: {}, status: {}", 
                request.getRoomId(), request.getAllMic(), request.getAllMute(), request.getStatus());
        
        if (StringUtils.isBlank(request.getRoomId())) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }
        
        // 查询会议室（兼容历史重复数据：按更新时间倒序取最新一条）
        List<RoomListV2Entity> rooms = roomListV2Repository.findByRoomIdOrderByUpdateTimeDesc(request.getRoomId());
        if (rooms == null || rooms.isEmpty()) {
            if ("destroyed".equals(request.getStatus())) {
                log.info("销毁成功：roomId: {}", request.getRoomId());
                return true;
            }
            throw new RuntimeException("会议室不存在");
        }
        RoomListV2Entity room = rooms.get(0);
        
        // 更新payload中的设置
        Map<String, Object> payload = room.getPayload();
        if (payload == null) {
            payload = new java.util.HashMap<>();
        }
        
        // 仅在前端传入对应字段时才更新，未传的不改动
        if (request.getAllMic() != null) {
            payload.put("allMic", request.getAllMic());
        }
        if (request.getAllMute() != null) {
            payload.put("allMute", request.getAllMute());
        }
        
        // 更新房间实体
        room.setPayload(payload);
        room.setUpdateTime(System.currentTimeMillis());
        
        // 如果传入了状态，则更新状态
        if (StringUtils.isNotBlank(request.getStatus())) {
            // 验证状态转换合法性
            if (!isValidStatusTransition(room.getStatus(), request.getStatus())) {
                throw new IllegalArgumentException("状态转换不合法：从 " + room.getStatus() + " 到 " + request.getStatus());
            }
            room.setStatus(request.getStatus());
        }
        
        // 保存到数据库
        roomListV2Repository.save(room);
        
        log.info("updateRoomSettings, success, roomId: {}, status: {}", request.getRoomId(), room.getStatus());
        return true;
    }
    
    /**
     * 从payload中提取聊天室配置（用于RoomDetailDto）
     * 
     * @param payload 负载数据
     * @return 聊天室配置
     */
    private RoomDetailDto.ChatRoomConfigDto extractRoomDetailChatRoomConfig(Map<String, Object> payload) {
        if (payload == null) {
            return buildRoomDetailChatRoomConfig("默认会议室", 1000);
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> chatRoomConfig = (Map<String, Object>) payload.get("chatRoomConfig");
        if (chatRoomConfig == null) {
            return buildRoomDetailChatRoomConfig("默认会议室", 1000);
        }
        
        return new RoomDetailDto.ChatRoomConfigDto()
                .setMaxUsers((Integer) chatRoomConfig.getOrDefault("maxUsers", 1000))
                .setName((String) chatRoomConfig.getOrDefault("name", "默认会议室"));
    }
    
    /**
     * 构建聊天室配置（用于RoomDetailDto）
     * 
     * @param name 聊天室名称
     * @param maxUsers 最大用户数
     * @return 聊天室配置
     */
    private RoomDetailDto.ChatRoomConfigDto buildRoomDetailChatRoomConfig(String name, int maxUsers) {
        return new RoomDetailDto.ChatRoomConfigDto()
                .setMaxUsers(maxUsers)
                .setName(name);
    }
}
