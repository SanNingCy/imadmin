package com.seekweb4.chat.agora.bean.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Map;

/**
 * RTC频道用户实体类
 * 
 * <p>用于存储声网RTC频道中用户的活动记录和状态信息。</p>
 * <p>该实体类对应MongoDB集合，用于持久化用户在频道中的行为数据。</p>
 * 
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li><b>用户会话管理</b> - 记录用户加入/离开频道的时间和状态</li>
 *   <li><b>角色跟踪</b> - 记录用户在频道中的角色变化（主播/观众）</li>
 *   <li><b>行为统计</b> - 统计用户在频道的停留时长、活跃度等</li>
 *   <li><b>状态监控</b> - 实时跟踪用户在线状态和权限</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@Document(collection = "rtc_channel_users")
public class RtcChannelUserEntity {
    
    /**
     * 用户会话唯一标识符
     * 格式：appId + "_" + channelName + "_" + uid + "_" + sessionId
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
     * 用户ID
     */
    private String uid;
    
    /**
     * 会话ID
     * 用于区分同一用户在同一频道的不同会话
     */
    private String sessionId;
    
    /**
     * 用户状态
     * - online: 在线状态
     * - offline: 离线状态
     * - banned: 被封禁状态
     */
    private String status;
    
    /**
     * 用户角色
     * - broadcaster: 主播
     * - audience: 观众
     * - communicator: 通信用户（通信模式下）
     */
    private String role;
    
    /**
     * 用户加入频道时间（时间戳，毫秒）
     */
    private Long joinTime;
    
    /**
     * 用户离开频道时间（时间戳，毫秒）
     * null表示用户仍在频道中
     */
    private Long leaveTime;
    
    /**
     * 最后活跃时间（时间戳，毫秒）
     */
    private Long lastActiveTime;
    
    /**
     * 在频道停留时长（毫秒）
     * 仅在用户离开时计算并更新
     */
    private Long duration;
    
    /**
     * 用户权限列表
     * 记录用户在频道中拥有的权限
     */
    private java.util.List<String> privileges;
    
    /**
     * 用户设备信息
     */
    private String deviceInfo;
    
    /**
     * 用户网络信息
     */
    private String networkInfo;
    
    /**
     * 用户IP地址
     */
    private String ipAddress;
    
    /**
     * 用户地理位置信息
     */
    private String location;
    
    /**
     * 用户客户端版本
     */
    private String clientVersion;
    
    /**
     * 用户连接质量评分
     * 0-100，数值越高表示连接质量越好
     */
    private Integer connectionQuality;
    
    /**
     * 角色变更历史
     * 记录用户在该会话中的角色变更记录
     */
    private java.util.List<Map<String, Object>> roleHistory;
    
    /**
     * 扩展属性
     * 用于存储业务相关的自定义数据
     */
    private Map<String, Object> properties;
    
    /**
     * 备注信息
     */
    private String remarks;
    
    /**
     * 记录创建时间（时间戳，毫秒）
     */
    private Long createTime;
    
    /**
     * 记录更新时间（时间戳，毫秒）
     */
    private Long updateTime;
}