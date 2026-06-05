package com.seekweb4.chat.agora.bean.req.v2;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class ChatRoomCreateReq {
    @SerializedName("appId")
    @NotBlank(message = "appId is blank")
    private String appId;

    @SerializedName("type")
    @NotNull(message = "type is null")
    private Integer type;


    @SerializedName("chatRoomConfig")
    private ChatRoomConfig chatRoomConfig;

    @SerializedName("imConfig")
    private ImConfig imConfig;


    @SerializedName("user")
    private User user;

    @Data
    @Accessors(chain = true)
    public static class ChatRoomConfig {
        @SerializedName("name")
        @NotBlank(message = "聊天室名称不能为空")
        private String name; // 会议ID

        @SerializedName("description")
        private String description;

        @SerializedName("maxusers")
        private int maxUsers;

        @SerializedName("custom")
        private String custom;
    }

    @Data
    @Accessors(chain = true)
    public static class ImConfig {
        @SerializedName("orgName")
        private String orgName;

        @SerializedName("appName")
        private String appName;

        @SerializedName("clientId")
        private String clientId;

        @SerializedName("clientSecret")
        private String clientSecret;
    }

    @Data
    @Accessors(chain = true)
    public static class User {
        @SerializedName("username")
        private String username;

        @SerializedName("password")
        private String password;
    }
}
