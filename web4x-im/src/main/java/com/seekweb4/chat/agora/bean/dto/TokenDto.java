package com.seekweb4.chat.agora.bean.dto;

import lombok.Data;

@Data
public class TokenDto {
    // AppId
    private String appId;
    // RTC token
    private String rtcToken;
    // RTM token
    private String rtmToken;
}
