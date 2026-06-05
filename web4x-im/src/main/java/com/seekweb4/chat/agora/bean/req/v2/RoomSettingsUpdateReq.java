package com.seekweb4.chat.agora.bean.req.v2;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 会议室设置更新请求
 * 
 * @author liangbo
 * @since 2024-01-01
 */
@Data
@Accessors(chain = true)
public class RoomSettingsUpdateReq {
    
    /**
     * 会议室ID
     */
    @NotBlank(message = "会议室ID不能为空")
    private String roomId;
    
    /**
     * 全员开麦
     */
//    @NotNull(message = "全员开麦设置不能为空")
    private Boolean allMic;
    
    /**
     * 全员禁言
     */
//    @NotNull(message = "全员禁言设置不能为空")
    private Boolean allMute;
    
    /**
     * 会议室状态
     * - pending_create : 待创建
     * - active: 房间活跃状态，有用户在线
     * - inactive: 房间非活跃状态，无用户在线
     * - destroyed: 房间已销毁
     */
    @Pattern(regexp = "^(pending_create|active|inactive|destroyed)$", message = "会议室状态只能是pending_create、active、inactive或destroyed")
    private String status;
}
