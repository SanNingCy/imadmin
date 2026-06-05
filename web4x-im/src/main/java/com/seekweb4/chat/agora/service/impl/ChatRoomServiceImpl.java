package com.seekweb4.chat.agora.service.impl;

import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomAPICreateChatRoomDto;
import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomAPICreateUserDto;
import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomAPIQueryUserDto;
import io.agora.chat.ChatTokenBuilder2;

import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPICreateChatRoomReq;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPICreateUserReq;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPIGetUserTokenReq;
import com.seekweb4.chat.agora.service.IChatRoomService;
import com.seekweb4.chat.agora.service.api.ChatRoomAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 聊天室API服务实现类
 * 
 * <p>聊天室API服务的具体实现，整合了环信API服务和本地token生成功能。</p>
 * <p>该服务支持缓存机制，提高token获取的性能。</p>
 * 
 * @author Agora
 * @see IChatRoomService
 * @see ChatRoomAPIService
 */
@Slf4j
@Service
public class ChatRoomServiceImpl implements IChatRoomService {
    
    /** 环信API服务，用于调用环信后端接口 */
    @Resource
    private ChatRoomAPIService chatRoomAPIService;

    /** 应用ID，从配置文件读取 */
    @Value("${whitelist.token.appId}")
    private String appId;
    
    /** 应用证书，从配置文件读取 */
    @Value("${whitelist.token.appCert}")
    private String appCert;

    /** token过期时间（秒），从配置文件读取 */
    @Value("${whitelist.token.expirePeriod}")
    private Integer expirePeriod;

    /**
     * 获取应用级别token
     * 
     * <p>使用本地ChatTokenBuilder2生成应用token，而非调用远程API。</p>
     * <p>token支持缓存，相同clientID的请求将直接返回缓存结果。</p>
     * 
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param clientID 客户端ID（用作缓存key）
     * @param clientSecret 客户端密钥
     * @return 应用token字符串
     */
    @Override
    //@Cacheable(cacheNames = "chatRoomAPIAppToken", key = "#clientID")
    public String getAppToken(String orgName, String appName, String clientID, String clientSecret) {
        log.info("getAppToken,orgName:{},appName:{},clientID:{},clientSecret:{}", orgName, appName, clientID, clientSecret);
        ChatTokenBuilder2 builder = new ChatTokenBuilder2();
        String appToken = builder.buildAppToken(appId, appCert, expirePeriod);
        log.info("appToken: {}",appToken);
        return appToken;
    }

    /**
     * 获取用户token
     * 
     * <p>通过环信API服务获取指定用户的访问token。</p>
     * <p>支持缓存，缓存key由组织名、应用名和用户名组成。</p>
     * 
     * @param username 用户名
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param token 应用token，用于API认证
     * @return 用户访问token
     */
    @Override
    //@Cacheable(cacheNames = "chatRoomAPIUserToken", key = "#orgName+'_'+#appName+'_'+#username")
    public String getUserToken(String username, String orgName, String appName, String token) {
        log.info("getUserToken,username:{},orgName:{},appName:{}", username, orgName, appName);
        return chatRoomAPIService.GetUserToken(
                new ChatRoomAPIGetUserTokenReq().setUsername(username),
                orgName, appName, token
        ).getAccessToken();
    }

    /**
     * 创建聊天室用户
     * 
     * <p>通过环信API创建新用户。</p>
     * 
     * @param req 用户创建请求参数
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param token 应用token
     * @return 用户创建结果
     */
    @Override
    public ChatRoomAPICreateUserDto createUser(ChatRoomAPICreateUserReq req, String orgName, String appName, String token) {
        return chatRoomAPIService.CreateUser(req, orgName, appName, token);
    }

    /**
     * 查询用户信息
     * 
     * <p>通过环信API查询指定用户的详细信息。</p>
     * 
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param username 要查询的用户名
     * @param token 应用token
     * @return 用户查询结果
     */
    @Override
    public ChatRoomAPIQueryUserDto queryUser(String orgName, String appName, String username, String token) {
        return chatRoomAPIService.QueryUser(orgName, appName, username, token);
    }

    /**
     * 创建聊天室
     * 
     * <p>通过环信API创建新的聊天室。</p>
     * 
     * @param req 聊天室创建请求参数
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param token 应用token
     * @return 聊天室创建结果
     */
    @Override
    public ChatRoomAPICreateChatRoomDto createChatRoom(ChatRoomAPICreateChatRoomReq req, String orgName, String appName, String token) {
        return chatRoomAPIService.CreateChatRoom(req, orgName, appName, token);
    }
}
