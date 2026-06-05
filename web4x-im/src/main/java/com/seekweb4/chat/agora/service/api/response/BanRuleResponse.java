package com.seekweb4.chat.agora.service.api.response;

import lombok.Data;

/**
 * 封禁规则操作响应
 * 
 * @author liangbo
 */
@Data
public class BanRuleResponse {
    
    /**
     * 请求状态
     * "success" 表示请求成功
     */
    private String status;
    
    /**
     * 规则ID
     * 保存规则ID以便稍后更新或删除此规则
     * 仅在创建规则时返回
     */
    private Long id;
}