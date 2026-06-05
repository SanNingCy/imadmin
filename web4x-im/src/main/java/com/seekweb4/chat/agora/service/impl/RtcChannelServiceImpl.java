package com.seekweb4.chat.agora.service.impl;

import com.seekweb4.chat.agora.bean.dto.CreateKickOutRuleDto;
import com.seekweb4.chat.agora.bean.req.CreateKickOutRule;
import com.seekweb4.chat.agora.service.IRtcChannelService;
import com.seekweb4.chat.agora.service.api.RtcChannelAPIService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * RTC频道服务实现类
 * 
 * <p>RTC频道服务的具体实现，主要处理用户踢出等频道管理功能。</p>
 * <p>封装了底层的RTC频道API调用，提供更高级的业务接口。</p>
 * 
 * @author Agora
 * @see IRtcChannelService
 * @see RtcChannelAPIService
 */
@Service
public class RtcChannelServiceImpl implements IRtcChannelService {
    
    /** RTC频道API服务，用于底层API调用 */
    @Resource
    private RtcChannelAPIService rtcChannelAPIService;

    /**
     * 踢出指定用户
     * 
     * <p>通过创建踢出规则来实现用户踢出功能。</p>
     * <p>会创建一个包含指定权限和时长的封禁规则，立即生效。</p>
     * 
     * @param basicAuth Basic认证字符串，用于API认证
     * @param appId 项目的App ID
     * @param cname 频道名称
     * @param uid 要踢出的用户ID
     * @param time 封禁时长（分钟），0表示永久封禁
     * @param privileges 要封禁的权限列表
     * @throws Exception 踢出操作失败时抛出异常
     */
    @Override
    public void kickOut(String basicAuth, String appId, String cname, Long uid, Integer time, List<String> privileges) throws Exception {
        // 构建踢出规则
        CreateKickOutRule rule = new CreateKickOutRule()
                .setAppId(appId)
                .setCname(cname)
                .setIp("")  // 不基于IP进行踢出
                .setUid(uid)
                .setTime(time)
                .setPrivileges(privileges);
        
        // 调用API创建踢出规则
        CreateKickOutRuleDto kickOutRuleDTO = rtcChannelAPIService.createKickOutRule(rule, basicAuth);
        if (kickOutRuleDTO == null) {
            throw new Exception("failed to kick out user");
        }
    }
}
