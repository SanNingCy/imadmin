package com.seekweb4.chat.agora.service.api;

import com.seekweb4.chat.agora.bean.dto.v2.*;
import com.seekweb4.chat.agora.bean.dto.v2.*;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPICreateChatRoomReq;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPICreateUserReq;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPIGetAppTokenReq;
import com.seekweb4.chat.agora.bean.req.v2.ChatRoomAPIGetUserTokenReq;

/**
 * 环信(EM)API服务接口
 * 使用Feign客户端调用环信聊天服务的REST API
 */
public interface ChatRoomAPIService {

    /**
     * 获取应用token
     *
     * @param req 获取应用token请求参数
     * @param orgName 组织名称
     * @param appName 应用名称
     * @return 应用token响应结果
     */
    @RequestLine("POST /{org_name}/{app_name}/token")
    @Headers("Content-Type:application/json")
    ChatRoomAPIGetAppTokenDto GetAppToken(ChatRoomAPIGetAppTokenReq req, @Param("org_name") String orgName, @Param("app_name") String appName);

    /**
     * 获取用户token
     *
     * @param req 获取用户token请求参数
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param token 应用授权token
     * @return 用户token响应结果
     */
    @RequestLine("POST /{org_name}/{app_name}/token")
    @Headers({"Authorization: {basicAuth}", "Content-Type:application/json"})
    ChatRoomAPIGetUserTokenDto GetUserToken(ChatRoomAPIGetUserTokenReq req, @Param("org_name") String orgName, @Param("app_name") String appName, @Param("basicAuth") String token);

    /**
     * 创建用户
     *
     * @param req 创建用户请求参数
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param token 应用授权token
     * @return 用户创建响应结果
     */
    @RequestLine("POST /{org_name}/{app_name}/users")
    @Headers({"Authorization: {basicAuth}", "Content-Type:application/json"})
    ChatRoomAPICreateUserDto CreateUser(ChatRoomAPICreateUserReq req, @Param("org_name") String orgName, @Param("app_name") String appName, @Param("basicAuth") String token);

    /**
     * 查询用户信息
     *
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param username 用户名
     * @param token 应用授权token
     * @return 用户查询响应结果
     */
    @RequestLine("GET /{org_name}/{app_name}/users/{username}")
    @Headers("Authorization: {basicAuth}")
    ChatRoomAPIQueryUserDto QueryUser(@Param("org_name") String orgName, @Param("app_name") String appName, @Param("username") String username, @Param("basicAuth") String token);

    /**
     * 创建聊天室
     *
     * @param req 创建聊天室请求参数
     * @param orgName 组织名称
     * @param appName 应用名称
     * @param token 应用授权token
     * @return 聊天室创建响应结果
     */
    @RequestLine("POST /{org_name}/{app_name}/chatrooms")
    @Headers({"Authorization: {basicAuth}", "Content-Type:application/json"})
    ChatRoomAPICreateChatRoomDto CreateChatRoom(ChatRoomAPICreateChatRoomReq req, @Param("org_name") String orgName, @Param("app_name") String appName, @Param("basicAuth") String token);

    @RequestLine("DELETE /{org_name}/{app_name}/chatrooms/{chatRoomId}")
    @Headers({"Authorization: {basicAuth}", "Content-Type:application/json"})
    ChatRoomAPICreateChatRoomDto deleteChatRoom(@Param("org_name") String orgName, @Param("app_name") String appName, @Param("basicAuth") String token,@Param("chatRoomId") String chatRoomId);


}
