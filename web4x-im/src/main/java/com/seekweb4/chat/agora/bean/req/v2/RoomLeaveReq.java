package com.seekweb4.chat.agora.bean.req.v2;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class RoomLeaveReq {
    // Room id
    @NotBlank(message = "roomId cannot be empty")
    private String roomId;

    // User id
    @NotBlank(message = "userId cannot be empty")
    private String userId;
}
