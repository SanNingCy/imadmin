package com.seekweb4.chat.agora.bean.req.v2;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Data
@Accessors(chain = true)
public class RoomCreateReq {
    /**
     * 应用ID（可选，后端自动使用默认值）
     */
    @NotBlank(message = "appId不能为空")
    private String appId;

    /**
     * 场景ID（可选，后端自动生成）
     */
    private String sceneId;

    /**
     * 会议ID（可选，后端自动生成）
     */
    private String roomId;

    // Room payload
    private Map<String, Object> payload;

    /**
     * 群ID，关联的群聊ID
     * 用于查询群内正在进行的会议
     */
    private String groupId;

    /**
     * 全员开麦（可选）。未传时后端默认使用 true
     */
    private Boolean allMic;

    /**
     * 全员禁言（可选）。未传时后端默认使用 false
     */
    private Boolean allMute;

    /**
     * 会议室时长（分钟）
     *
     * <p>会议室的使用时长，到期后自动销毁。</p>
     * <p>需要消耗对应的积分，时长越长消耗积分越多。</p>
     */
    private Integer durationMinutes;

    /**
     * 当前人数
     * 
     * <p>会议室当前在线人数，默认为1（创建者）。</p>
     * <p>随着用户加入和离开会动态变化。</p>
     */
    private Integer currentUsers;
}
