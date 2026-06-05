package com.seekweb4.chat.agora.service;

import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomAPICreateChatRoomDto;
import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomAPICreateUserDto;
import com.seekweb4.chat.agora.bean.dto.v2.ChatRoomAPIQueryUserDto;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPICreateChatRoomReq;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPICreateUserReq;

/**
 * 聊天室API服务接口
 * 提供聊天室相关的API操作，包括token管理、用户管理和聊天室管理
 */
public interface IChatRoomService {
    
    /**
     * 获取应用级别token
     * 
     * @param orgName 组织名称
     * @param appName 应用名称  
     * @param clientID 客户端ID
     * @param clientSecret 客户端密钥
     * @return 应用token
     */
    String getAppToken(String orgName, String appName, String clientID, String clientSecret);

    /**
     * 获取用户token
     * 
     * @param username 用户名
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param token 应用token
     * @return 用户token
     */
    String getUserToken(String username, String orgName, String appName, String token);

    /**
     * 创建聊天室用户
     * 
     * @param req 创建用户请求参数
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param token 应用token
     * @return 用户创建结果
     */
    ChatRoomAPICreateUserDto createUser(ChatRoomAPICreateUserReq req, String orgName, String appName, String token);

    /**
     * 查询聊天室用户信息
     * 
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param username 用户名
     * @param token 应用token
     * @return 用户查询结果
     */
    ChatRoomAPIQueryUserDto queryUser(String orgName, String appName, String username, String token);

    /**
     * 创建聊天室
     * 
     * @param req 创建聊天室请求参数
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param token 应用token
     * @return 聊天室创建结果
     */
    ChatRoomAPICreateChatRoomDto createChatRoom(ChatRoomAPICreateChatRoomReq req, String orgName, String appName, String token);
}
