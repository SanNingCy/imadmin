package com.seekweb4.chat.agora.service.api.request;

import lombok.Data;

import java.util.List;

/**
 * 更新封禁规则请求
 * 
 * @author liangbo
 */
@Data
public class BanRuleUpdateRequest {
    
    /**
     * 项目的App ID (必填)
     */
    private String appid;
    
    /**
     * 频道名称 (可选)
     */
    private String cname;
    
    /**
     * 用户ID (可选)
     * 不要设置为0
     */
    private Long uid;
    
    /**
     * 用户IP地址 (可选)
     * 不要设置为0
     */
    private String ip;
    
    /**
     * 封禁时长（分钟）
     * 取值范围：[1,1440]
     */
    private Integer time;
    
    /**
     * 封禁时长（秒）
     * 取值范围：[10,86430]
     */
    private Integer timeInSeconds;
    
    /**
     * 要封禁的用户权限
     * 可选值：
     * - "join_channel": 禁止用户加入频道或将用户踢出频道
     * - "publish_audio": 禁止用户发布音频
     * - "publish_video": 禁止用户发布视频
     */
    private List<String> privileges;
}