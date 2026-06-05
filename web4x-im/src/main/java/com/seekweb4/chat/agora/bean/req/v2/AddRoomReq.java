package com.seekweb4.chat.agora.bean.req.v2;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class AddRoomReq {
    private String id;
    private String appId;

    private String sceneId;

    private String roomId;

    // payload
    private Map<String, Object> payload;

    /**
     * 群ID，关联的群聊ID
     */
    private String groupId;

    /**
     * 群主ID/房间创建者
     */
    private String ownerId;

    // update time
    private long updateTime;
    // Create time
    private Long createTime;
}
