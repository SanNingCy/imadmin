package com.seekweb4.chat.agora.service.webhook.strategy;

import com.seekweb4.chat.agora.bean.enums.ChannelEventTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * Webhook事件处理策略工厂
 * 
 * <p>负责管理和路由webhook事件到对应的处理策略。</p>
 * <p>基于工厂模式和策略模式，提供事件类型到处理策略的映射和路由功能。</p>
 * 
 * <p><b>主要功能：</b></p>
 * <ul>
 *   <li><b>策略注册</b> - 自动发现和注册所有可用的处理策略</li>
 *   <li><b>策略路由</b> - 根据事件类型路由到对应的处理策略</li>
 *   <li><b>优先级管理</b> - 支持策略优先级，处理同一事件类型的多个策略</li>
 *   <li><b>异常处理</b> - 处理未知事件类型和策略缺失的情况</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookEventStrategyFactory {
    
    private final List<WebhookEventProcessStrategy> allStrategies;
    private final Map<ChannelEventTypeEnum, List<WebhookEventProcessStrategy>> strategyMap = new HashMap<>();
    
    @PostConstruct
    public void init() {
        log.info("初始化Webhook事件处理策略工厂，发现策略数量: {}", allStrategies.size());
        
        // 按事件类型分组策略
        for (WebhookEventProcessStrategy strategy : allStrategies) {
            ChannelEventTypeEnum eventType = strategy.getSupportedEventType();
            strategyMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(strategy);
            
            log.info("注册策略: {} -> {}", eventType, strategy.getClass().getSimpleName());
        }
        
        log.info("策略工厂初始化完成，支持的事件类型: {}", strategyMap.keySet());
    }
    
    /**
     * 根据事件类型获取处理策略
     * 
     * @param eventType 事件类型
     * @return 处理策略，如果没有找到则返回null
     */
    public WebhookEventProcessStrategy getStrategy(ChannelEventTypeEnum eventType) {
        if (eventType == null) {
            log.warn("事件类型不能为null");
            return null;
        }
        
        List<WebhookEventProcessStrategy> strategies = strategyMap.get(eventType);
        if (strategies == null || strategies.isEmpty()) {
            log.warn("未找到事件类型 {} 的处理策略", eventType);
            return null;
        }
        
        // 返回优先级最高的策略（优先级数值最小）
        WebhookEventProcessStrategy selectedStrategy = strategies.get(0);
        log.debug("为事件类型 {} 选择策略: {}", eventType, selectedStrategy.getClass().getSimpleName());
        
        return selectedStrategy;
    }
    
    /**
     * 根据事件类型代码获取处理策略
     * 
     * @param eventTypeCode 事件类型代码
     * @return 处理策略，如果没有找到则返回null
     */
    public WebhookEventProcessStrategy getStrategy(Integer eventTypeCode) {
        if (eventTypeCode == null) {
            log.warn("事件类型代码不能为null");
            return null;
        }
        
        ChannelEventTypeEnum eventType = ChannelEventTypeEnum.getStrategyEventType(eventTypeCode);
        if (eventType == null) {
            log.warn("未知的事件类型代码: {}", eventTypeCode);
            return null;
        }
        
        return getStrategy(eventType);
    }
    
    /**
     * 获取指定事件类型的所有策略
     * 
     * @param eventType 事件类型
     * @return 策略列表，按优先级排序
     */
    public List<WebhookEventProcessStrategy> getAllStrategies(ChannelEventTypeEnum eventType) {
        return strategyMap.getOrDefault(eventType, Collections.emptyList());
    }
    
    /**
     * 获取所有支持的事件类型
     * 
     * @return 支持的事件类型集合
     */
    public Set<ChannelEventTypeEnum> getSupportedEventTypes() {
        return Collections.unmodifiableSet(strategyMap.keySet());
    }
    
    /**
     * 检查是否支持指定的事件类型
     * 
     * @param eventType 事件类型
     * @return 是否支持
     */
    public boolean isSupported(ChannelEventTypeEnum eventType) {
        return eventType != null && strategyMap.containsKey(eventType);
    }
    
    /**
     * 检查是否支持指定的事件类型代码
     * 
     * @param eventTypeCode 事件类型代码
     * @return 是否支持
     */
    public boolean isSupported(Integer eventTypeCode) {
        if (eventTypeCode == null) {
            return false;
        }
        
        ChannelEventTypeEnum eventType = ChannelEventTypeEnum.getStrategyEventType(eventTypeCode);
        return isSupported(eventType);
    }
    
    /**
     * 获取策略统计信息
     * 
     * @return 策略统计信息
     */
    public Map<String, Object> getStrategyStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalStrategies", allStrategies.size());
        stats.put("supportedEventTypes", strategyMap.size());
        
        Map<String, Integer> eventTypeStats = new HashMap<>();
        strategyMap.forEach((eventType, strategies) -> {
            eventTypeStats.put(eventType.name(), strategies.size());
        });
        stats.put("eventTypeStats", eventTypeStats);
        
        Map<String, String> strategyList = new HashMap<>();
        allStrategies.forEach(strategy -> {
            strategyList.put(strategy.getClass().getSimpleName(), strategy.getSupportedEventType().name());
        });
        stats.put("strategyList", strategyList);
        
        return stats;
    }
    
    /**
     * 重新初始化策略工厂
     * 
     * <p>通常在运行时动态添加策略后调用</p>
     */
    public void refresh() {
        strategyMap.clear();
        init();
    }
    
    /**
     * 根据策略类名获取策略实例
     * 
     * @param strategyClassName 策略类名
     * @return 策略实例，如果没有找到则返回null
     */
    public WebhookEventProcessStrategy getStrategyByClassName(String strategyClassName) {
        if (strategyClassName == null || strategyClassName.trim().isEmpty()) {
            return null;
        }
        
        return allStrategies.stream()
                .filter(strategy -> strategy.getClass().getSimpleName().equals(strategyClassName) ||
                                  strategy.getClass().getName().equals(strategyClassName))
                .findFirst()
                .orElse(null);
    }
}