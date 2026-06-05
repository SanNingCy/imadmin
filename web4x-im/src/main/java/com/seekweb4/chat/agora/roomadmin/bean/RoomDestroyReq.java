package com.seekweb4.chat.agora.roomadmin.bean;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 会议解散请求对象
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
@Data
public class RoomDestroyReq {

    /**
     * 房间ID
     */
    @NotBlank(message = "房间ID不能为空")
    private String roomId;

    /**
     * 解散原因
     */
    private String reason;

    /**
     * 操作者ID
     */
//    @NotBlank(message = "操作者ID不能为空")
    private String operatorId;
}
