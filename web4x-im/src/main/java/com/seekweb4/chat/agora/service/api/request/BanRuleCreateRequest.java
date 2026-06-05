package com.seekweb4.chat.agora.service.api.request;

import lombok.Data;

import java.util.List;

/**
 * 创建封禁规则请求
 * 
 * @author liangbo
 */
@Data
public class BanRuleCreateRequest {
    
    /**
     * 项目的App ID (必填)
     * 可通过以下方式获取：
     * - 从Agora Console复制
     * - 调用获取所有项目API，读取响应体中vendor_key字段的值
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
     * 封禁时长（分钟） (必填)
     * 取值范围：[1,1440]
     * 
     * 注意：
     * - 如果设置值在0和1之间，Agora自动设置为1
     * - 如果设置值大于1440，Agora自动设置为1440
     * - 如果设置值为0，封禁规则不生效，服务器将符合规则的所有用户下线，用户可重新登录加入频道
     * 
     * 使用time或time_in_seconds中的一个。如果两个参数都设置，time_in_seconds参数生效；
     * 如果两个参数都不设置，Agora服务器自动设置封禁时长为60分钟，即3600秒
     */
    private Integer time;
    
    /**
     * 封禁时长（秒） (必填)
     * 取值范围：[10,86430]
     * 
     * 注意：
     * - 如果设置值在0和10之间，Agora自动设置为10
     * - 如果设置值大于86430，Agora自动设置为86430
     * - 如果设置值为0，封禁规则不生效，服务器将符合规则的所有用户下线，用户可重新登录加入频道
     * 
     * 使用time或time_in_seconds中的一个。如果两个参数都设置，time_in_seconds参数生效；
     * 如果两个参数都不设置，Agora服务器自动设置封禁时长为60分钟，即3600秒
     */
    private Integer timeInSeconds;
    
    /**
     * 要封禁的用户权限 (必填)
     * 可选值：
     * - "join_channel": 禁止用户加入频道或将用户踢出频道
     * - "publish_audio": 禁止用户发布音频
     * - "publish_video": 禁止用户发布视频
     * 
     * 可以传入publish_audio和publish_video来同时禁止用户发布音频和视频
     */
    private List<String> privileges;
}