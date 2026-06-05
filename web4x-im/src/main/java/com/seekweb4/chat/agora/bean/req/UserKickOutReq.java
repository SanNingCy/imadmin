package com.seekweb4.chat.agora.bean.req;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UserKickOutReq {
    @NotBlank(message = "appId cannot be empty")
    private String appId;

    private String basicAuth;

    @NotBlank(message = "roomId cannot be empty")
    private String roomId;

    @NotNull(message = "uid cannot be empty")
    private Long uid;
}
