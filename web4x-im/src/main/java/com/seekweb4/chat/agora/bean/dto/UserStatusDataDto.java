package com.seekweb4.chat.agora.bean.dto;

import lombok.Data;

/**
 * 用户状态数据DTO
 * 
 * @author Agora
 */
@Data
public class UserStatusDataDto {
    
    /**
     * 用户是否在频道中
     */
    private Boolean inChannel;
    
    /**
     * 用户是否为主播
     */
    private Boolean isHost;
    
    /**
     * 用户角色
     */
    private Integer role;
    
    /**
     * 用户是否在线
     * true: 用户在线
     * false: 用户不在线
     */
    private Boolean online;
}