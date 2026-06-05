package com.seekweb4.chat.agora.bean.dto;

import lombok.Data;

import java.util.List;

/**
 * 封禁规则列表响应DTO
 * 
 * @author Agora
 */
@Data
public class BanRuleListResponseDto {
    
    /**
     * 请求状态
     * "success" 表示请求成功
     */
    private String status;
    
    /**
     * 封禁规则列表
     * 包含多个封禁规则信息的数组
     */
    private List<BanRuleInfoDto> rules;
}