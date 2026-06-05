package com.seekweb4.chat.agora.service.api.response;

import lombok.Data;

import java.util.List;

/**
 * 封禁规则列表响应
 * 
 * @author liangbo
 */
@Data
public class BanRuleListResponse {
    
    /**
     * 请求状态
     * "success" 表示请求成功
     */
    private String status;
    
    /**
     * 封禁规则列表
     * 包含多个封禁规则信息的数组
     */
    private List<BanRuleInfo> rules;
}