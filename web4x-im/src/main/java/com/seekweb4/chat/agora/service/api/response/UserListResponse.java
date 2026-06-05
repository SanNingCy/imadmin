package com.seekweb4.chat.agora.service.api.response;

import lombok.Data;

/**
 * 用户列表响应
 * 
 * @author liangbo
 */
@Data
public class UserListResponse {
    
    /**
     * 请求状态
     * true: 成功
     * false: 保留供将来使用
     */
    private Boolean success;
    
    /**
     * 用户信息
     */
    private UserListData data;
}