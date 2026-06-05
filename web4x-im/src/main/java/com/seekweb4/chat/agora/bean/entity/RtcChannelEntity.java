package com.seekweb4.chat.agora.bean.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.Map;

/**
 * RTC频道实体类
 * 
 * <p>用于存储声网RTC频道的基本信息和状态数据。</p>
 * <p>该实体类对应MongoDB集合，用于持久化频道管理相关数据。</p>
 * 
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li><b>频道基础信息</b> - 存储频道名称、应用ID等基本信息</li>
 *   <li><b>用户统计</b> - 记录当前在线用户数、历史最大用户数等</li>
 *   <li><b>状态管理</b> - 跟踪频道创建、活跃、销毁状态</li>
 *   <li><b>扩展数据</b> - 支持存储自定义业务数据</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@Document(collection = "rtc_channels")
public class RtcChannelEntity {
    
    /**
     * 频道唯一标识符
     * 格式：appId + "_" + channelName
     */
    @MongoId
    private String id;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 频道名称
     */
    private String channelName;
    
    /**
     * 频道状态
     * - active: 活跃状态，有用户在线
     * - inactive: 非活跃状态，无用户在线
     * - destroyed: 已销毁状态
     */
    private String status;
    
    /**
     * 当前在线用户数
     */
    private Integer currentUserCount;
    
    /**
     * 历史最大用户数
     */
    private Integer maxUserCount;
    
    /**
     * 当前在线用户ID列表
     */
    private List<String> currentUserIds;
    
    /**
     * 频道创建时间（时间戳，毫秒）
     */
    private Long createTime;
    
    /**
     * 最后更新时间（时间戳，毫秒）
     */
    private Long updateTime;
    
    /**
     * 最后活跃时间（时间戳，毫秒）
     * 用于判断频道是否应该被清理
     */
    private Long lastActiveTime;
    
    /**
     * 频道模式
     * - live: 直播模式
     * - communication: 通信模式
     */
    private String channelMode;
    
    /**
     * 扩展属性
     * 用于存储业务相关的自定义数据
     */
    private Map<String, Object> properties;
    
    /**
     * 频道描述信息
     */
    private String description;
    
    /**
     * 频道所有者用户ID
     */
    private String ownerId;
    
    /**
     * 频道总累计用户数
     * 记录从创建以来所有加入过的用户数量
     */
    private Integer totalUserCount;
    
    /**
     * 频道配置信息
     * 存储频道相关的配置参数
     */
    private Map<String, Object> config;
}