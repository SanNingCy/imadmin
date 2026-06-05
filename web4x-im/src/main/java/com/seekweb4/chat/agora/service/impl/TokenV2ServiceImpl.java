package com.seekweb4.chat.agora.service.impl;

import com.seekweb4.chat.agora.bean.config.TokenConfig;
import com.seekweb4.chat.agora.bean.dto.TokenDto;
import com.seekweb4.chat.agora.config.WhitelistConfig;
import com.seekweb4.chat.agora.service.ITokenV2Service;
import com.seekweb4.chat.agora.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * Token V2服务实现类
 * 
 * <p>Token V2服务的具体实现，提供各种类型token的生成功能。</p>
 * <p>支持RTC token、RTM token的生成，以及新旧版本格式的兼容。</p>
 * <p>使用白名单配置管理不同项目的应用凭证。</p>
 * 
 * @author Agora
 * @see ITokenV2Service
 * @see TokenUtil
 */
@Slf4j
@Service
public class TokenV2ServiceImpl implements ITokenV2Service {
    
    /** Token工具类，用于具体的token生成逻辑 */
    @Resource
    private TokenUtil tokenUtil;

    /** Token过期时间配置（秒），默认24小时 */
    @Value("${token.expirationInSeconds}")
    private int tokenExpirationInSeconds = 3600 * 24;
    
    /** 特权过期时间配置（秒），默认24小时 */
    @Value("${token.privilegeExpirationInSeconds}")
    private int privilegeExpirationInSeconds = 3600 * 24;

    /** 白名单配置，管理项目凭证 */
    @Resource
    private WhitelistConfig whiteListConfig;

    /**
     * 生成RTC token (最新版本)
     * 
     * <p>生成用于RTC实时音视频通信的访问token。</p>
     * 
     * @param appId 应用ID
     * @param appCert 应用证书
     * @param channelName 频道名称
     * @param account 用户账户标识
     * @return RTC token字符串
     */
    @Override
    public String generateRtcToken(String appId, String appCert, String channelName, String account) {
        log.info("generateRtcToken, appId:{}, channelName:{}, account:{}", appId, channelName, account);
        return tokenUtil.generateRtcToken(appId, appCert, channelName, account,
                tokenExpirationInSeconds, privilegeExpirationInSeconds);
    }

    /**
     * 生成RTC token (006版本)
     * 
     * <p>生成006版本格式的RTC token，用于向后兼容。</p>
     * 
     * @param appId 应用ID
     * @param appCert 应用证书
     * @param channelName 频道名称
     * @param account 用户账户标识
     * @return RTC token字符串（006格式）
     */
    @Override
    public String generateRtcToken006(String appId, String appCert, String channelName, String account) {
        log.info("generateRtcToken006, appId:{}, channelName:{}, account:{}", appId, channelName, account);
        return tokenUtil.generateRtcToken006(appId, appCert, channelName, account,
                privilegeExpirationInSeconds);
    }

    /**
     * 生成RTM token (最新版本)
     * 
     * <p>生成用于RTM实时消息传递的访问token。</p>
     * 
     * @param appId 应用ID
     * @param appCert 应用证书
     * @param userId 用户ID
     * @return RTM token字符串
     */
    @Override
    public String generateRtmToken(String appId, String appCert, String userId) {
        log.info("generateRtmToken, appId:{}, userId:{}", appId, userId);
        return tokenUtil.generateRtmToken(appId, appCert, userId, tokenExpirationInSeconds);
    }

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
    @Override
    public String generateRtmToken006(String appId, String appCert, String userId) throws Exception {
        log.info("generateRtmToken006, appId:{}, userId:{}", appId, userId);
        return tokenUtil.generateRtmToken006(appId, appCert, userId, tokenExpirationInSeconds);
    }

    /**
     * 生成Token对象 (最新版本)
     * 
     * <p>生成包含RTC和RTM token的完整Token对象。</p>
     * <p>首先从白名单配置中获取正确的应用凭证，然后生成对应的token。</p>
     * 
     * @param appId 应用ID（用于查找白名单配置）
     * @param appCert 应用证书（用于查找白名单配置）
     * @param channelName 频道名称
     * @param account 用户账户标识
     * @return Token对象，包含RTC和RTM token
     */
    @Override
    public TokenDto generateToken(String appId, String appCert, String channelName, String account) {
        log.info("getToken, appId:{}, channelName:{}, account:{}", appId, channelName, account);
        
        // 从白名单配置中获取token配置
        TokenConfig token = whiteListConfig.getTokenFromWhitelist(appId, appCert);
        appId = token.getAppId();
        appCert = token.getAppCert();
        
        // 生成RTC和RTM token
        TokenDto tokenDto = new TokenDto();
        String rtcToken = generateRtcToken(appId, appCert, channelName, account);
        String rtmToken = generateRtmToken(appId, appCert, account);
        
        tokenDto.setAppId(appId);
        tokenDto.setRtcToken(rtcToken);
        tokenDto.setRtmToken(rtmToken);

        return tokenDto;
    }

    /**
     * 生成Token对象 (006版本)
     * 
     * <p>生成006版本格式的完整Token对象，用于向后兼容。</p>
     * 
     * @param appId 应用ID
     * @param appCert 应用证书
     * @param channelName 频道名称
     * @param account 用户账户标识
     * @return Token对象（006格式）
     * @throws Exception token生成过程中的异常
     */
    @Override
    public TokenDto generateToken006(String appId, String appCert, String channelName, String account) throws Exception {
        log.info("generateToken006, appId:{}, channelName:{}, account:{}", appId, channelName, account);
        
        // 从白名单配置中获取token配置
        TokenConfig token = whiteListConfig.getTokenFromWhitelist(appId, appCert);
        appId = token.getAppId();
        appCert = token.getAppCert();
        
        // 生成RTC和RTM token（006版本）
        TokenDto tokenDto = new TokenDto();
        String rtcToken = generateRtcToken006(appId, appCert, channelName, account);
        String rtmToken = generateRtmToken006(appId, appCert, account);
        
        tokenDto.setAppId(appId);
        tokenDto.setRtcToken(rtcToken);
        tokenDto.setRtmToken(rtmToken);

        return tokenDto;
    }
}
