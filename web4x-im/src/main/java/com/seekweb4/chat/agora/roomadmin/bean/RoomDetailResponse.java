package com.seekweb4.chat.agora.roomadmin.bean;

import lombok.Data;

/**
 * 会议详情响应对象
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
@Data
public class RoomDetailResponse {

    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 群ID
     */
    private String groupId;

    /**
     * 创建者ID
     */
    private String ownerId;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 会议名称
     */
    private String meetingName;

    /**
     * 房间描述
     */
    private String description;

    /**
     * 状态
     * - active: 活跃状态，有用户在线
     * - inactive: 非活跃状态，无用户在线
     * - destroyed: 已销毁状态
     * - pending_create: 待创建状态
     */
    private String status;

    /**
     * 是否被封禁
     */
    private Boolean isBanned;

    /**
     * 当前用户数
     */
    private Integer userCount;

    /**
     * 最大用户数
     */
    private Integer maxUsers;

    /**
     * 是否全员开麦
     */
    private Boolean allMic;

    /**
     * 是否全员禁言
     */
    private Boolean allMute;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 聊天室ID
     */
    private String chatRoomId;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 最后活跃时间
     */
    private Long lastActiveTime;

    /**
     * 封禁原因
     */
    private String banReason;

    /**
     * 封禁时间
     */
    private Long banTime;

    /**
     * 负载数据
     */
    private Object payload;

    /**
     * 聊天室配置
     */
    private ChatRoomConfig chatRoomConfig;

    /**
     * 聊天室配置
     */
    @Data
    public static class ChatRoomConfig {
        /**
         * 最大用户数
         */
        private Integer maxUsers;

        /**
         * 聊天室名称
         */
        private String name;

        /**
         * 聊天室描述
         */
        private String description;
    }
}
