package com.seekweb4.chat.admin.service;

import lombok.Data;

/**
 * API响应包装类
 * 包含HTTP状态码和响应体
 */
@Data
public class ApiResponse {
    /**
     * HTTP状态码
     */
    private int statusCode;
    
    /**
     * 响应体内容
     */
    private String body;
    
    /**
     * 是否成功（状态码200-299视为成功）
     */
    private boolean success;
    
    public ApiResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
        this.success = statusCode >= 200 && statusCode < 300;
    }
}

