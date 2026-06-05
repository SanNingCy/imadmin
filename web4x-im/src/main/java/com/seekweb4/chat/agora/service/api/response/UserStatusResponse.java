package com.seekweb4.chat.agora.service.api.response;

import lombok.Data;

/**
 * 用户状态响应
 * 
 * @author liangbo
 */
@Data
public class UserStatusResponse {
    
    /**
     * 请求状态
     * true: 成功
     * false: 保留供将来使用
     */
    private Boolean success;
    
    /**
     * 用户状态信息
     */
    private UserStatusData data;
}