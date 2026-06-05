package com.seekweb4.chat.agora.bean.dto;

import lombok.Data;

import java.util.List;

/**
 * 封禁规则信息DTO
 * 
 * @author Agora
 */
@Data
public class BanRuleInfoDto {
    
    /**
     * 规则ID
     */
    private Long id;
    
    /**
     * 项目的App ID
     */
    private String appid;
    
    /**
     * 频道名称
     */
    private String cname;
    
    /**
     * 用户ID
     */
    private Long uid;
    
    /**
     * 用户IP地址
     */
    private String ip;
    
    /**
     * 封禁时长（秒）
     */
    private Integer ts;
    
    /**
     * 被封禁的用户权限列表
     */
    private List<String> privileges;
    
    /**
     * 规则创建时间的Unix时间戳（毫秒）
     */
    private Long createAt;
    
    /**
     * 规则更新时间的Unix时间戳（毫秒）
     */
    private Long updateAt;
}