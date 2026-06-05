package com.seekweb4.chat.agora.roomadmin.bean;

import lombok.Data;

import java.util.List;

/**
 * 会议列表响应对象
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
@Data
public class RoomListResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 会议列表
     */
    private List<RoomInfo> list;

    /**
     * 会议信息
     */
    @Data
    public static class RoomInfo {
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
         * 负载数据
         */
        private Object payload;
    }
}
