package com.seekweb4.chat.agora.bean.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;

/**
 * 封禁规则实体类
 * 
 * <p>用于存储声网RTC用户封禁规则的完整信息。</p>
 * <p>该实体类对应MongoDB集合，用于持久化用户权限管理数据。</p>
 * 
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li><b>规则管理</b> - 存储封禁规则的完整配置信息</li>
 *   <li><b>权限控制</b> - 记录用户被封禁的具体权限类型</li>
 *   <li><b>时间管理</b> - 跟踪规则创建、生效、过期时间</li>
 *   <li><b>状态监控</b> - 实时跟踪封禁规则的执行状态</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@Document(collection = "ban_rules")
public class BanRuleEntity {
    
    /**
     * 封禁规则唯一标识符
     * 来源于声网API返回的规则ID
     */
    @MongoId
    private String id;
    
    /**
     * 声网返回的规则ID
     * 用于与声网API进行规则管理操作
     */
    private Long ruleId;
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 频道名称
     * null表示适用于所有频道
     */
    private String channelName;
    
    /**
     * 用户ID
     * null表示适用于所有用户
     */
    private String uid;
    
    /**
     * IP地址
     * 支持基于IP的封禁
     */
    private String ipAddress;
    
    /**
     * 封禁的权限列表
     * 可选值：join_channel, publish_audio, publish_video
     */
    private java.util.List<String> privileges;
    
    /**
     * 封禁时长（秒）
     * 0表示永久封禁
     */
    private Integer time;
    
    /**
     * 时长单位
     * - second: 秒
     * - minute: 分钟
     */
    private String timeUnit;
    
    /**
     * 规则状态
     * - active: 生效中
     * - expired: 已过期
     * - cancelled: 已取消
     */
    private String status;
    
    /**
     * 规则创建时间（时间戳，毫秒）
     */
    private Long createTime;
    
    /**
     * 规则生效时间（时间戳，毫秒）
     */
    private Long effectiveTime;
    
    /**
     * 规则过期时间（时间戳，毫秒）
     * null表示永不过期
     */
    private Long expireTime;
    
    /**
     * 规则更新时间（时间戳，毫秒）
     */
    private Long updateTime;
    
    /**
     * 规则创建者
     */
    private String createdBy;
    
    /**
     * 封禁原因
     */
    private String reason;
    
    /**
     * 规则描述
     */
    private String description;
    
    /**
     * 封禁类型
     * - user: 用户封禁
     * - channel: 频道封禁
     * - ip: IP封禁
     */
    private String banType;
    
    /**
     * 严重级别
     * - low: 低级
     * - medium: 中级
     * - high: 高级
     * - critical: 严重
     */
    private String severity;
    
    /**
     * 相关的事件ID
     * 关联触发该封禁规则的事件记录
     */
    private String relatedEventId;
    
    /**
     * 自动解封标志
     * true表示到期自动解封，false表示需要手动解封
     */
    private Boolean autoUnban;
    
    /**
     * 执行次数
     * 记录该规则被触发的次数
     */
    private Integer executeCount;
    
    /**
     * 最后执行时间（时间戳，毫秒）
     */
    private Long lastExecuteTime;
    
    /**
     * 扩展属性
     */
    private Map<String, Object> properties;
    
    /**
     * 备注信息
     */
    private String remarks;
}