package com.seekweb4.chat.agora.bean.req.v2;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class RoomListReq {
    @NotBlank(message = "appId cannot be empty")
    private String appId;

    @NotBlank(message = "sceneId cannot be empty")
    private String sceneId;

    private Long lastCreateTime;

    // Page size
    @Max(value = 50, message = "pageSize must be less than or equal to 50")
    @Min(value = 1, message = "pageSize must be greater than 0")
    private int pageSize;
}
