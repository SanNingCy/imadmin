package com.seekweb4.chat.agora.bean.dto.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class RoomDto {
    private String appId;
    private String sceneId;
    private String roomId;
    private Map<String, Object> payload;
    private Long updateTime;
    private Long createTime;
    private String groupId;
    /**
     * 房间状态
     * - active: 房间活跃状态，有用户在线
     * - inactive: 房间非活跃状态，无用户在线
     * - destroyed: 房间已销毁
     */
    private String status;
}
