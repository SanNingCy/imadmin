package com.seekweb4.chat.agora.bean.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.Sharded;

import java.util.Map;

@Data
@Accessors(chain = true)
@Document(collection = "uikit_room_list_v2")
public class RoomListV2Entity {
    /**
     * 会议名称
     */
    private String meetingName;

    private String appId;
    private String sceneId;
    private String chatRoomId;

    @Indexed(unique = true)
    private String roomId;

    @Id
    private String id;

    // payload
    private Map<String, Object> payload;

    /**
     * 频道状态
     * - active: 活跃状态，有用户在线
     * - inactive: 非活跃状态，无用户在线
     * - destroyed: 已销毁状态
     */
    private String status;

    /**
     * 最后活跃时间（时间戳，毫秒）
     * 用于判断频道是否应该被清理
     */
    private Long lastActiveTime;

    /**
     * 频道所有者用户ID，创建人用户ID
     */
    private String ownerId;

    /**
     * 群ID，关联的群聊ID
     * 用于查询群内正在进行的会议
     */
    private String groupId;

    // 最后更新时间（时间戳，毫秒）
    private Long updateTime;
    // Create time
    private Long createTime;

    private String idNo;
    private String roomName;
    private Boolean allMic;
    private Boolean allMute;
    private Long startTime; // epoch millis
    private String timeZone;
    private Integer meetingMaxUsers; // 100|300|600|1000
    private Integer meetingTime; // 30|60|90|120|150|180 (minutes)
    private Integer currentUsers; // 当前人数，预创建默认1
    private Long endTime; // 结束时间（毫秒）
    private Boolean isNonPayPwd; // 是否免密支付
    private String meetingTimeName;
    private Integer minMeetingTime;
    private String minMeetingTimeName;

    private String groupName;
    private String ownerNickname;
}
