package com.seekweb4.chat.agora.service;

import com.seekweb4.chat.agora.bean.dto.TokenDto;

/**
 * Token V2服务接口
 * 提供Agora平台的各类token生成服务，包括RTC和RTM token
 */
public interface ITokenV2Service {
    
    /**
     * 生成RTC token (最新版本)
     * 
     * <p>生成用于RTC实时音视频通信的访问token，支持最新的token格式和安全特性。</p>
     *
     * @param appId 应用ID，从Agora Console获取
     * @param appCert 应用证书，从Agora Console获取
     * @param channelName 频道名称，用户要加入的频道
     * @param account 用户账户标识，通常为用户ID
     * @return RTC token字符串
     */
    String generateRtcToken(String appId, String appCert, String channelName, String account);

    /**
     * 生成RTC token (006版本)
     * 
     * <p>生成006版本格式的RTC token，用于向后兼容旧版本SDK。</p>
     *
     * @param appId 应用ID
     * @param appCert 应用证书
     * @param channelName 频道名称
     * @param account 用户账户标识
     * @return RTC token字符串（006格式）
     */
    String generateRtcToken006(String appId, String appCert, String channelName, String account);

    /**
     * 生成RTM token (最新版本)
     * 
     * <p>生成用于RTM实时消息传递的访问token。</p>
     *
     * @param appId 应用ID
     * @param appCert 应用证书
     * @param userId 用户ID，RTM服务的用户标识
     * @return RTM token字符串
     */
    String generateRtmToken(String appId, String appCert, String userId);

    /**
     * 生成RTM token (006版本)
     * 
     * <p>生成006版本格式的RTM token，用于向后兼容。</p>
     *
     * @param appId 应用ID
     * @param appCert 应用证书
     * @param userId 用户ID
     * @return RTM token字符串（006格式）
     * @throws Exception token生成过程中的异常
     */
    String generateRtmToken006(String appId, String appCert, String userId) throws Exception;

    /**
     * 生成Token对象 (最新版本)
     * 
     * <p>生成包含RTC和RTM token的完整Token对象，方便客户端同时获取两种token。</p>
     *
     * @param appId 应用ID
     * @param appCert 应用证书
     * @param channelName 频道名称
     * @param account 用户账户标识
     * @return Token对象，包含RTC和RTM token
     */
    TokenDto generateToken(String appId, String appCert, String channelName, String account);

    /**
     * 生成Token对象 (006版本)
     * 
     * <p>生成006版本格式的完整Token对象。</p>
     *
     * @param appId 应用ID
     * @param appCert 应用证书
     * @param channelName 频道名称
     * @param account 用户账户标识
     * @return Token对象（006格式）
     * @throws Exception token生成过程中的异常
     */
    TokenDto generateToken006(String appId, String appCert, String channelName, String account) throws Exception;
}
