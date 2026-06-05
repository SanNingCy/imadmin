package com.seekweb4.chat.agora.roomadmin.bean;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 会议详情查询请求对象
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
@Data
public class RoomDetailQueryReq {

    /**
     * 房间ID
     */
    @NotBlank(message = "房间ID不能为空")
    private String roomId;
}
