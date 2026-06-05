package com.seekweb4.chat.agora.service;

import com.seekweb4.chat.agora.bean.enums.ChannelEventTypeEnum;
import com.seekweb4.chat.agora.bean.req.WebhookNotificationReq;
import com.seekweb4.chat.agora.bean.req.v2.EventCallBackReq;
import com.seekweb4.chat.agora.utils.WebhookSignatureUtil;

/**
 * Webhook事件处理服务接口
 * 
 * <p>定义声网RTC Webhook事件的处理规范，包括事件处理和签名验证功能。</p>
 * <p>所有webhook相关的业务逻辑都应该通过此接口进行抽象，便于测试和扩展。</p>
 * 
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li><b>事件处理</b> - 处理各种RTC频道事件</li>
 *   <li><b>签名验证</b> - 验证webhook消息的真实性和完整性</li>
 *   <li><b>事件分发</b> - 根据事件类型分发到具体的处理逻辑</li>
 *   <li><b>异常处理</b> - 统一处理事件处理过程中的异常</li>
 * </ul>
 * 
 * <p><b>实现要求：</b></p>
 * <ul>
 *   <li>所有方法都应该是幂等的，支持重复调用</li>
 *   <li>事件处理失败不应该影响其他事件的处理</li>
 *   <li>应该记录详细的处理日志，便于问题排查</li>
 *   <li>支持异步处理，避免阻塞webhook响应</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 * @see WebhookNotificationReq
 */
public interface IWebhookEventService {
    
    /**
     * 处理webhook事件通知
     * 
     * <p>接收并处理声网服务器发送的webhook事件通知。</p>
     * <p>方法会根据事件类型自动分发到对应的处理器进行处理。</p>
     * 
     * <p><b>处理流程：</b></p>
     * <ol>
     *   <li>验证请求参数的有效性</li>
     *   <li>根据eventType识别具体的事件类型</li>
     *   <li>分发事件到对应的处理器</li>
     *   <li>记录处理结果和日志</li>
     *   <li>异常情况下进行错误处理</li>
     * </ol>
     * 
     * <p><b>事件去重：</b></p>
     * <p>实现应该基于noticeId进行去重处理，避免重复处理同一个事件。</p>
     * 
     * <p><b>异步处理：</b></p>
     * <p>为了快速响应webhook请求，建议使用异步方式处理复杂的业务逻辑。</p>
     * 
     * @param request webhook通知请求对象，包含事件的详细信息
     * 
     * @throws IllegalArgumentException 当请求参数无效时抛出
     * @throws RuntimeException 当事件处理过程中发生不可恢复的错误时抛出
     * 
     * @see ChannelEventTypeEnum
     */
    void handleWebhookEvent(EventCallBackReq request);

    /**
     * 获取支持的事件类型列表
     * 
     * <p>返回当前系统支持处理的所有webhook事件类型。</p>
     * <p>用于系统监控和调试，了解系统的处理能力范围。</p>
     * 
     * @return 支持的事件类型编码列表
     */
    java.util.Set<Integer> getSupportedEventTypes();
}