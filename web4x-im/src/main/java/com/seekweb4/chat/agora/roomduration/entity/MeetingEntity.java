package com.seekweb4.chat.agora.roomduration.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Accessors(chain = true)
@Document(collection = "meetings")
public class MeetingEntity {
    @Id
    private String id;

    private String roomId;
    private String idNo;
    private String groupId;
    private String roomName;
    private String ownerId;
    private Boolean allMic;
    private Boolean allMute;
    private Long startTime; // epoch millis
    private String timeZone;
    private Integer meetingMaxUsers; // 100|300|600|1000
    private Integer meetingTime; // 30|60|90|120|150|180 (minutes)
    private Integer currentUsers; // 当前人数，预创建默认1
    private Long endTime; // 结束时间（毫秒）
    private Boolean isNonPayPwd; // 是否免密支付
    private String status; // 房间状态：pending_create(待创建)、active(活跃)、inactive(非活跃)、destroyed(已销毁)
    private String meetingTimeName;
    private Integer minMeetingTime;
    private String minMeetingTimeName;

    private Long createTime;
    private Long updateTime;
}


