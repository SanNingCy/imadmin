package com.seekweb4.chat.agora.service;

import java.util.List;

/**
 * RTC频道服务接口
 * 提供RTC频道相关的业务操作，包括用户踢出等功能
 */
public interface IRtcChannelService {
    
    /**
     * 踢出指定用户
     * 
     * <p>将指定用户从频道中踢出，并可设置封禁权限和时长。</p>
     * 
     * @param basicAuth Basic认证字符串，格式：Base64("客户ID:客户密钥")
     * @param appId 项目的App ID
     * @param cname 频道名称
     * @param uid 要踢出的用户ID
     * @param time 封禁时长（分钟），0表示永久封禁
     * @param privileges 要封禁的权限列表，如："join_channel", "publish_audio", "publish_video"
     * @throws Exception 踢出操作过程中的异常
     */
    void kickOut(String basicAuth, String appId, String cname, Long uid, Integer time, List<String> privileges) throws Exception;
}
