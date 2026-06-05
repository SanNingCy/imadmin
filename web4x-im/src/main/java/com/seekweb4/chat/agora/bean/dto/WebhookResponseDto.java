package com.seekweb4.chat.agora.bean.dto;

import lombok.Data;

/**
 * Webhook处理响应DTO
 * 
 * <p>用于封装webhook事件处理的结果信息，向声网服务器返回处理状态。</p>
 * <p>声网服务器根据返回的HTTP状态码和响应内容判断事件是否被正确处理。</p>
 * 
 * <p><b>响应规范：</b></p>
 * <ul>
 *   <li>HTTP 200 - 事件处理成功</li>
 *   <li>HTTP 4xx/5xx - 事件处理失败，声网可能会重试</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 */
@Data
public class WebhookResponseDto {
    
    /**
     * 处理状态码
     * 
     * <p>业务层面的处理结果状态码：</p>
     * <ul>
     *   <li>200 - 处理成功</li>
     *   <li>400 - 请求格式错误</li>
     *   <li>401 - 签名验证失败</li>
     *   <li>500 - 内部处理错误</li>
     * </ul>
     */
    private Integer code;
    
    /**
     * 处理结果消息
     * 
     * <p>描述处理结果的详细信息，便于调试和问题排查。</p>
     */
    private String message;
    
    /**
     * 处理的事件数据
     * 
     * <p>可选字段，包含处理过程中产生的额外数据。</p>
     */
    private Object data;
    
    /**
     * 请求处理时间戳
     * 
     * <p>服务器处理完成的时间戳，用于性能监控和问题排查。</p>
     */
    private Long timestamp;
    
    /**
     * 构造成功响应
     * 
     * @return 成功响应对象
     */
    public static WebhookResponseDto success() {
        WebhookResponseDto response = new WebhookResponseDto();
        response.code = 200;
        response.message = "OK";
        response.timestamp = System.currentTimeMillis();
        return response;
    }
    
    /**
     * 构造成功响应（带数据）
     * 
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static WebhookResponseDto success(Object data) {
        WebhookResponseDto response = success();
        response.data = data;
        return response;
    }
    
    /**
     * 构造错误响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static WebhookResponseDto error(Integer code, String message) {
        WebhookResponseDto response = new WebhookResponseDto();
        response.code = code;
        response.message = message;
        response.timestamp = System.currentTimeMillis();
        return response;
    }
}