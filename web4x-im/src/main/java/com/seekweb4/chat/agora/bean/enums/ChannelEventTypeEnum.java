package com.seekweb4.chat.agora.bean.enums;

/**
 * RTC频道事件类型枚举
 * 
 * <p>定义声网RTC服务支持的所有频道事件类型。</p>
 * <p>这些事件类型对应声网服务器发送的webhook通知中的eventType字段。</p>
 * 
 * <p><b>事件分类：</b></p>
 * <ul>
 *   <li><b>直播场景事件</b> - 区分主播和观众角色</li>
 *   <li><b>通信场景事件</b> - 所有用户角色相同</li>
 *   <li><b>角色切换事件</b> - 用户在主播和观众间切换</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 * @see <a href="https://docs.agora.io/cn/Interactive%20Broadcast/rtc_channel_event">RTC频道事件文档</a>
 */
public enum ChannelEventTypeEnum {
    
    /**
     * 观众加入频道
     * 
     */
    USER_JOIN_CHANNEL(105, "观众加入频道", "观众加入频道事件"),
    /**
     * 观众离开频道
     *
     */
    USER_LEAVE_CHANNEL(106, "观众离开频道", "观众离开频道事件");

    /**
     * 事件类型编码
     */
    private final Integer eventType;
    
    /**
     * 事件名称
     */
    private final String eventName;
    
    /**
     * 事件描述
     */
    private final String description;
    
    ChannelEventTypeEnum(Integer eventType, String eventName, String description) {
        this.eventType = eventType;
        this.eventName = eventName;
        this.description = description;
    }
    
    public Integer getEventType() {
        return eventType;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据事件类型编码获取枚举实例
     * 
     * @param eventType 事件类型编码
     * @return 对应的枚举实例，如果不存在则返回null
     */
    public static ChannelEventTypeEnum fromEventType(Integer eventType) {
        if (eventType == null) {
            return null;
        }
        
        for (ChannelEventTypeEnum type : values()) {
            if (type.getEventType().equals(eventType)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 根据事件类型编码获取枚举实例（别名方法）
     * 
     * @param eventTypeCode 事件类型编码
     * @return 对应的枚举实例，如果不存在则返回null
     */
    public static ChannelEventTypeEnum fromCode(Integer eventTypeCode) {
        return fromEventType(eventTypeCode);
    }
    
    /**
     * 根据具体事件类型获取对应的通用策略类型
     * 
     * <p>将具体的事件类型映射到通用的策略类型，用于策略工厂路由。</p>
     * 
     * @param eventTypeCode 具体的事件类型编码
     * @return 对应的通用策略类型，如果不支持则返回null
     */
    public static ChannelEventTypeEnum getStrategyEventType(Integer eventTypeCode) {
        if (eventTypeCode == null) {
            return null;
        }
        return null;
    }

    /**
     * 判断是否为直播场景事件
     * 
     * @return true表示是直播场景事件
     */
    public boolean isLiveBroadcastEvent() {
        return eventType >= 101 && eventType <= 199;
    }
    
    /**
     * 判断是否为通信场景事件
     * 
     * @return true表示是通信场景事件
     */
    public boolean isCommunicationEvent() {
        return eventType >= 201 && eventType <= 299;
    }
}