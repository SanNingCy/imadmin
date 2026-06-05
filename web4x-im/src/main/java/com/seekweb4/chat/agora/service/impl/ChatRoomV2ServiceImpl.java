package com.seekweb4.chat.agora.service.impl;

import com.seekweb4.chat.agora.bean.config.ChatRoomConfig;
import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomAPICreateChatRoomDto;
import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomAPIQueryUserDto;
import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomCreateDto;
import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPICreateChatRoomReq;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPICreateUserReq;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomCreateReq;
import com.seekweb4.chat.agora.config.WhitelistConfig;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.service.IChatRoomService;
import com.seekweb4.chat.agora.service.IChatRoomV2Service;
import com.seekweb4.chat.agora.service.api.ChatRoomAPIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 聊天室V2服务实现类
 * 
 * <p>聊天室V2服务的具体实现，提供聊天室创建的完整流程。</p>
 * <p>支持三种创建模式：用户+聊天室、仅用户、仅聊天室。</p>
 * 
 * @author Agora
 * @see IChatRoomV2Service
 */
@Slf4j
@Service
public class ChatRoomV2ServiceImpl implements IChatRoomV2Service {
    
    /** 聊天室API服务，用于与环信后端交互 */
    @Resource
    private ChatRoomAPIService chatRoomAPIService;

    /** 白名单配置，包含聊天室相关配置信息 */
    @Resource
    private WhitelistConfig whiteListConfig;
    /** 白名单配置，包含聊天室相关配置信息 */
    @Resource
    private IChatRoomService iChatRoomService;

    @Resource
    @Lazy()
    private RoomListV2Repository roomListV2Repository;

    @Value("${whitelist.token.appId}")
    private String appId;

    /**
     * 创建聊天室
     * 
     * <p>根据请求类型执行不同的创建策略：</p>
     * <ul>
     *   <li>type=0: 创建用户和聊天室</li>
     *   <li>type=1: 仅创建用户</li>
     *   <li>type=2: 仅创建聊天室</li>
     * </ul>
     * 
     * <p>配置优先级：白名单配置 > 请求中的IM配置</p>
     * 
     * @param req 聊天室创建请求
     * @return 聊天室创建结果，包含用户token、用户UUID、聊天室ID等信息
     * @throws Exception 创建过程中的异常
     */
    @Override
    public ChatRoomCreateDto Create(ChatRoomCreateReq req) throws Exception {
        // 参数校验
        if (req == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }

        log.info("create,req:{}", req);
        ChatRoomCreateReq.ImConfig imConfig = new ChatRoomCreateReq.ImConfig();
        
        // 优先从白名单配置中获取聊天室配置
        ChatRoomConfig chatRoomFromWhitelist = whiteListConfig.getChatRoomFromWhitelist(appId);
        if (chatRoomFromWhitelist != null) {
            if (imConfig == null) {
                imConfig = new ChatRoomCreateReq.ImConfig();
            }

            imConfig.setOrgName(chatRoomFromWhitelist.getOrgName());
            imConfig.setAppName(chatRoomFromWhitelist.getAppName());
            imConfig.setClientId(chatRoomFromWhitelist.getClientId());
            imConfig.setClientSecret(chatRoomFromWhitelist.getClientSecret());
        }
        
        if (imConfig == null) {
            throw new Exception("im config is null");
        }
        
        // 获取应用token
        String appToken = getAppToken(imConfig.getOrgName(), imConfig.getAppName(), imConfig.getClientId(), imConfig.getClientSecret());
        log.info("acquire token:{}", appToken);
        
        ChatRoomCreateReq.User user = req.getUser();
        String userUuid = "";
        String userToken = "";
        //会议聊天室ID
        String chatRoomId = "";
        
        // 根据类型执行不同的创建逻辑
        switch (req.getType()) {
            // 创建用户和聊天室
            case 0:
                if (Strings.isBlank(user.getPassword())) {
                    user.setPassword(UUID.randomUUID().toString());
                }

                if (ObjectUtils.isEmpty(req.getChatRoomConfig()) || StringUtils.isBlank(req.getChatRoomConfig().getName())) {
                    throw new IllegalArgumentException("聊天室名称ID不能为空");
                }

                // TODO: 通过会议id(name)获取会议信息
                // req.getChatRoomConfig().getName()其实就是会议室ID
                Optional<RoomListV2Entity> rooms = roomListV2Repository.findByRoomId(req.getChatRoomConfig().getName());
                //TODO: 当会议信息不存在时，返回不存在会议室错误
                if (!rooms.isPresent()) {
                    throw new Exception("不存在会议室");
                }
                RoomListV2Entity room = rooms.get();

                userUuid = createUser(imConfig.getOrgName(), imConfig.getAppName(), appToken, user.getUsername(), user.getPassword());
                userToken = getUserToken(imConfig.getOrgName(), imConfig.getAppName(), appToken, user.getUsername());
                // TODO: 存在时，将当前聊天室信息（chatRoomId）注入到会议信息对象中
                chatRoomId  =  createRoom(imConfig.getOrgName(), imConfig.getAppName(), appToken, user.getUsername(), req.getChatRoomConfig());
                room.setChatRoomId(chatRoomId);
                roomListV2Repository.save(room);
                break;
            // 仅创建用户
            case 1:
                if (Strings.isBlank(user.getPassword())) {
                    user.setPassword(UUID.randomUUID().toString());
                }
                userUuid = createUser(imConfig.getOrgName(), imConfig.getAppName(), appToken, user.getUsername(), user.getPassword());
                userToken = getUserToken(imConfig.getOrgName(), imConfig.getAppName(), appToken, user.getUsername());
                break;
            // 仅创建聊天室
            case 2:
                if (ObjectUtils.isEmpty(req.getChatRoomConfig()) || StringUtils.isBlank(req.getChatRoomConfig().getName())) {
                    throw new IllegalArgumentException("聊天室名称ID不能为空");
                }

                // TODO: 通过会议id(name)获取会议信息
                // req.getChatRoomConfig().getName()其实就是会议室ID
                Optional<RoomListV2Entity> rooms2 = roomListV2Repository.findByRoomId(req.getChatRoomConfig().getName());
                //TODO: 当会议信息不存在时，返回不存在会议室错误
                if (!rooms2.isPresent()) {
                    throw new Exception("不存在会议室");
                }
                RoomListV2Entity room2 = rooms2.get();

                chatRoomId = createRoom(imConfig.getOrgName(), imConfig.getAppName(), appToken, user.getUsername(), req.getChatRoomConfig());
                // TODO: 存在时，注入聊天室信息（chatRoomId）到会议信息对象中并更新保存
                room2.setChatRoomId(chatRoomId);
                roomListV2Repository.save(room2);
                break;
            default:
                throw new Exception("invalid type");
        }

        // TODO: 组织返回结果
        return new ChatRoomCreateDto()
                .setUserToken(userToken)
                .setUserUuid(userUuid)
                .setChatId(chatRoomId)
                .setAppKey(imConfig.getOrgName() + "#" + imConfig.getAppName());
    }

    /**
     * 创建聊天室
     * 
     * <p>通过API创建新的聊天室，并将房主添加为成员。</p>
     * 
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param appToken 应用token
     * @param ownerName 房主用户名
     * @param chatRoomConfig 聊天室配置
     * @return 创建的聊天室ID
     * @throws Exception 创建过程中的异常
     */
    private String createRoom(String orgName, String appName, String appToken, String ownerName, ChatRoomCreateReq.ChatRoomConfig chatRoomConfig) throws Exception {
        ChatRoomAPICreateChatRoomDto createRoomAPIDto = chatRoomAPIService.CreateChatRoom(
                new ChatRoomAPICreateChatRoomReq()
                        .setName(chatRoomConfig.getName())
                        .setDescription(chatRoomConfig.getDescription())
                        .setMaxUsers(chatRoomConfig.getMaxUsers())
                        .setOwner(ownerName)
                        .setMembers(Arrays.asList(ownerName))
                        .setCustom(chatRoomConfig.getCustom()),
                orgName, appName, appToken
        );

        return createRoomAPIDto.getRoomId();
    }

    /**
     * 获取应用token
     * 
     * <p>通过聊天室API服务获取应用级别的访问token。</p>
     * 
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param clientID 客户端ID
     * @param clientSecret 客户端密钥
     * @return 应用token
     * @throws Exception 获取过程中的异常
     */
    public String getAppToken(String orgName, String appName, String clientID, String clientSecret) throws Exception {
        // ChatRoomAPIService使用Feign接口，需要先构建请求对象，然后调用API
//        var appTokenReq = new io.agora.uikit.bean.req.v2.ChatRoomAPIGetAppTokenReq()
//                .setClientId(clientID)
//                .setClientSecret(clientSecret)
//                .setGrantType("client_credentials");

        //var appTokenDto = chatRoomAPIService.GetAppToken(appTokenReq, orgName, appName);
        String appTokenDto2 = iChatRoomService.getAppToken(orgName, appName,clientID,clientSecret);
        return  "Bearer".concat(" ").concat(appTokenDto2);
    }

    /**
     * 创建或获取用户
     * 
     * <p>首先尝试查询用户是否存在，如果不存在则创建新用户。</p>
     * 
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param appToken 应用token
     * @param username 用户名
     * @param password 用户密码
     * @return 用户UUID
     * @throws Exception 操作过程中的异常
     */
    private String createUser(String orgName, String appName, String appToken, String username, String password) throws Exception {
        try {
            // 先尝试查询用户是否已存在
            ChatRoomAPIQueryUserDto chatRoomAPIQueryUserDto = chatRoomAPIService.QueryUser(orgName, appName, username, appToken);
            if (chatRoomAPIQueryUserDto != null) {
                return chatRoomAPIQueryUserDto.getUser().getUuid();
            }
        } catch (Exception ex) {
            log.info("query user err:{}", ex.getMessage());
        }
        
        // 用户不存在，创建新用户
        return chatRoomAPIService.CreateUser(
                new ChatRoomAPICreateUserReq()
                        .setUsername(username)
                        .setPassword(password),
                orgName, appName, appToken
        ).getUser().getUuid();
    }

    /**
     * 获取用户token
     * 
     * <p>通过聊天室API服务获取指定用户的访问token。</p>
     * 
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param appToken 应用token
     * @param username 用户名
     * @return 用户访问token
     * @throws Exception 获取过程中的异常
     */
    public String getUserToken(String orgName, String appName, String appToken, String username) throws Exception {
        return iChatRoomService.getUserToken(username, orgName, appName, appToken);
    }
}
