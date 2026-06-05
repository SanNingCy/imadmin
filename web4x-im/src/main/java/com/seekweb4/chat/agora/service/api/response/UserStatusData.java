package com.seekweb4.chat.agora.service.api.response;

import lombok.Data;

/**
 * 用户状态数据
 * 
 * @author liangbo
 */
@Data
public class UserStatusData {
    
    /**
     * 用户是否在线
     * true: 用户在线
     * false: 用户不在线
     */
    private Boolean online;
}