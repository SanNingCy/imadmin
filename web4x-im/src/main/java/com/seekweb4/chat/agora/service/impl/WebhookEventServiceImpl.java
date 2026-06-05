package com.seekweb4.chat.agora.service.impl;

import com.seekweb4.chat.agora.bean.enums.ChannelEventTypeEnum;
import com.seekweb4.chat.agora.bean.req.v2.EventCallBackReq;
import com.seekweb4.chat.agora.service.IWebhookEventService;
import com.seekweb4.chat.agora.service.webhook.strategy.WebhookEventProcessStrategy;
import com.seekweb4.chat.agora.service.webhook.strategy.WebhookEventStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Webhook事件处理服务实现类
 *
 * <p>实现声网RTC Webhook事件的接收、验证、分发和处理功能。</p>
 * <p>支持所有标准的RTC频道事件，包括用户加入/离开、角色切换等。</p>
 *
 * <p><b>核心特性：</b></p>
 * <ul>
 *   <li><b>事件去重</b> - 基于noticeId避免重复处理</li>
 *   <li><b>签名验证</b> - 支持SHA1和SHA256双重签名验证</li>
 *   <li><b>异步处理</b> - 异步处理复杂业务逻辑，快速响应webhook</li>
 *   <li><b>监控统计</b> - 记录事件处理统计信息</li>
 * </ul>
 *
 * @author Agora
 * @version 1.0
 */
@Slf4j
@Service
public class WebhookEventServiceImpl implements IWebhookEventService {

    /**
     * Webhook事件处理策略工厂
     */
    @Resource
    private WebhookEventStrategyFactory strategyFactory;

    @Override
    public void handleWebhookEvent(EventCallBackReq event) {
        if (event == null) {
            log.warn("接收到空的webhook请求");
            return;
        }
        if (event.getEventType() == null) {
            log.warn("接收到无效的webhook请求，缺少事件类型: {}", event);
            return;
        }
        // 获取事件类型信息
        ChannelEventTypeEnum eventType = ChannelEventTypeEnum.fromEventType(event.getEventType());
        String eventDescription = eventType != null ? eventType.getEventName() : "未知事件";
        String channelName = event.getPayload().get("channelName").toString();
        log.info("开始处理webhook事件 - 类型: {} ({}), 通知ID: {}, 频道: {}",
                event.getEventType(), eventDescription, event.getNoticeId(), channelName);
        try {
            // 根据事件类型进行处理
            processEventByType(event, eventType);
            log.info("成功处理webhook事件: noticeId={}, eventType={}", event.getNoticeId(), event.getEventType());
        } catch (Exception e) {
            log.error("处理webhook事件失败 - 事件类型: {}, 通知ID: {}", event.getEventType(), event.getNoticeId(), e);
        }
    }

    /**
     * 根据事件类型处理具体的业务逻辑
     *
     * @param event     webhook请求
     * @param eventType 事件类型枚举
     */
    private void processEventByType(EventCallBackReq event, ChannelEventTypeEnum eventType) {
        if (eventType == null) {
            log.warn("未识别的事件类型: {}, 执行默认处理", event.getEventType());
            return;
        }

        // 解析payload
        Map<String,Object> payload1 = event.getPayload();
        String channelName = payload1.get("channelName").toString();
        log.debug("处理事件详情 - 频道: {}, payload: {}", channelName, payload1);
        // 获取对应的处理策略
        WebhookEventProcessStrategy strategy = strategyFactory.getStrategy(eventType);
        if (strategy == null) {
            log.warn("未找到事件类型 {} 的处理策略，执行默认处理", eventType);
            return;
        }
        // 执行策略处理
        log.info("使用策略 {} 处理事件: {}", strategy.getClass().getSimpleName(), eventType);
        strategy.process(event);
    }

    @Override
    public Set<Integer> getSupportedEventTypes() {
        Set<Integer> supportedTypes = new HashSet<>();
        for (ChannelEventTypeEnum eventType : ChannelEventTypeEnum.values()) {
            supportedTypes.add(eventType.getEventType());
        }
        return supportedTypes;
    }

}