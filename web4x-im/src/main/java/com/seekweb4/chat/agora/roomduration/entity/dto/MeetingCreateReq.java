package com.seekweb4.chat.agora.roomduration.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;

@Data
@Accessors(chain = true)
public class MeetingCreateReq {
    @NotNull(message = "群ID不能为空")
    private String groupId;

    @NotBlank(message = "会议室名称不能为空")
    private String roomName;

    @NotBlank(message = "发起人用户ID不能为空")
    private String ownerId;

//    @NotNull(message = "是否全禁麦不能为空")
    private Boolean allMic;

//    @NotNull(message = "是否全禁言不能为空")
    private Boolean allMute;

    // 前端传 yyyy-MM-dd HH:mm:ss，后端解析为毫秒
//    @NotBlank(message = "开始时间不能为空")
    private String startTime;

//    @NotBlank(message = "时区不能为空")
    private String timeZone; // 例如 UTC+8

    @NotNull(message = "最大人数不能为空")
//    @Pattern(regexp = "^(100|300|600|1000)$", message = "最大人数仅支持100|300|600|1000")
    private String meetingMaxUsers;

    @NotNull(message = "会议时长不能为空")
//    @Pattern(regexp = "^(30|60|90|120|150|180)$", message = "会议时长仅支持30|60|90|120|150|180")
    private String meetingTime; // 分钟

     // 是否免密支付（可选），不传默认false
     private Boolean isNonPayPwd;
//     private Boolean noPasswordPay;
    //结束时间
    private Long endTime;

    //加时次数
    private Integer addSize;
}


