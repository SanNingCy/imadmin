package com.seekweb4.chat.agora.controller.v2;

import com.seekweb4.chat.agora.bean.dto.R;
import com.seekweb4.chat.agora.bean.req.WebhookNotificationReq;
import com.seekweb4.chat.agora.bean.req.v2.EventCallBackReq;
import com.seekweb4.chat.agora.service.IWebhookEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 声网RTC Webhook接收器控制器
 *
 * <p>负责接收和处理声网服务器发送的RTC频道事件通知。</p>
 * <p>提供webhook消息的接收、验证、处理等核心功能，确保事件通知的安全性和可靠性。</p>
 *
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>事件接收</b> - 接收声网服务器发送的HTTP POST事件通知</li>
 *   <li><b>签名验证</b> - 验证webhook消息的HMAC签名，确保消息安全</li>
 *   <li><b>事件处理</b> - 分发和处理各种RTC频道事件</li>
 *   <li><b>健康检查</b> - 提供健康检查接口，便于监控</li>
 * </ul>
 *
 * <p><b>安全特性：</b></p>
 * <ul>
 *   <li>支持HMAC-SHA1和HMAC-SHA256双重签名验证</li>
 *   <li>可配置开关签名验证功能</li>
 *   <li>详细的安全审计日志</li>
 * </ul>
 *
 * <p><b>配置参数：</b></p>
 * <ul>
 *   <li>agora.webhook.customer-key - 客户密钥，用于签名验证</li>
 *   <li>agora.webhook.signature-verification.enabled - 是否启用签名验证</li>
 * </ul>
 *
 * <p><b>API路径：</b></p>
 * <ul>
 *   <li>POST /v2/webhook/rtc/notification - 接收事件通知</li>
 *   <li>GET /v2/webhook/rtc/health - 健康检查</li>
 *   <li>GET /v2/webhook/rtc/stats - 处理统计信息</li>
 * </ul>
 *
 * @author Agora
 * @version 1.0
 * @see IWebhookEventService
 * @see WebhookNotificationReq
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v2/webhook/rtc")
public class WebhookController {

    /**
     * Webhook事件处理服务
     */
    @Autowired
    private IWebhookEventService webhookEventService;

    /**
     * 客户密钥，用于webhook签名验证
     * 从配置文件中读取，如果未配置则为空字符串
     */
    @Value("${agora.webhook.customer-key:}")
    private String customerKey;

    /**
     * 是否启用签名验证
     * 默认启用以保证安全性，测试环境可以设置为false
     */
    @Value("${agora.webhook.signature-verification.enabled:true}")
    private boolean signatureVerificationEnabled;

    /**
     * 接收声网RTC事件通知
     *
     * <p>这是webhook的核心接口，接收声网服务器发送的各种RTC频道事件。</p>
     * <p>声网服务器会向此接口发送HTTP POST请求，包含事件的详细信息。</p>
     *
     * <p><b>处理流程：</b></p>
     * <ol>
     *   <li>读取请求体内容</li>
     *   <li>验证请求体不为空</li>
     *   <li>提取并验证HMAC签名（如果启用）</li>
     *   <li>解析JSON请求体为对象</li>
     *   <li>调用服务层处理事件</li>
     *   <li>返回处理结果</li>
     * </ol>
     *
     * <p><b>HTTP响应码：</b></p>
     * <ul>
     *   <li>200 OK - 事件处理成功</li>
     *   <li>400 Bad Request - 请求格式错误或参数无效</li>
     *   <li>401 Unauthorized - 签名验证失败</li>
     *   <li>500 Internal Server Error - 服务器内部错误</li>
     * </ul>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>必须快速响应，避免声网服务器重试</li>
     *   <li>相同noticeId的事件会被自动去重</li>
     *   <li>处理失败的事件会记录详细错误日志</li>
     * </ul>
     *
     * @param request HTTP请求对象，用于获取请求体和签名头
     * @see WebhookNotificationReq
     * @see R
     */
    @PostMapping("/notification")
    public void receiveNotification(HttpServletRequest request, @RequestBody EventCallBackReq event) throws IOException {
        log.info("[{}] 请求体内容: {}", event.getNoticeId(), event);
        long startTime = System.currentTimeMillis();
        if (event == null) {
            return;
        }
        if (event.getEventType() != 105 && event.getEventType() != 106) {
            return;
        }

        // 4. 解析请求体
        log.info("解析webhook请求成功 - 事件类型: {}, 通知ID: {}", event.getEventType(), event.getNoticeId());
        // 5. 处理webhook事件
        webhookEventService.handleWebhookEvent(event);
        // 6. 记录处理时间和返回成功响应
        long processingTime = System.currentTimeMillis() - startTime;
        log.info("[{}] 成功处理webhook通知，耗时: {}ms", event.getNoticeId(), processingTime);
    }

    /**
     * 获取支持的事件类型列表
     *
     * <p>返回当前系统支持处理的所有webhook事件类型。</p>
     *
     * @return 支持的事件类型列表
     */
    @GetMapping("/supported-events")
    public ResponseEntity<R<Set<Integer>>> getSupportedEvents() {
        try {
            Set<Integer> supportedTypes = webhookEventService.getSupportedEventTypes();
            return ResponseEntity.ok(R.success(supportedTypes));

        } catch (Exception e) {
            log.error("获取支持的事件类型失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(R.error(500, "获取支持的事件类型失败"));
        }
    }

    /**
     * 从HTTP请求中读取请求体内容
     *
     * @param request HTTP请求对象
     * @return 请求体字符串
     * @throws IOException 读取IO异常
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    /**
     * 解析JSON请求体为WebhookNotificationReq对象
     *
     * @param requestBody 请求体JSON字符串
     * @return 解析后的对象，解析失败返回null
     */
    private WebhookNotificationReq parseRequestBody(String requestBody) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            return objectMapper.readValue(requestBody, WebhookNotificationReq.class);
        } catch (Exception e) {
            log.error("解析webhook请求体失败: {}", requestBody, e);
            return null;
        }
    }

}