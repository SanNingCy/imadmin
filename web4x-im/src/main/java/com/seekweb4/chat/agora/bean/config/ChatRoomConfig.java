package com.seekweb4.chat.agora.bean.config;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomConfig {
    private String appId;
    private String orgName;
    private String appName;
    private String clientId;
    private String clientSecret;
}
