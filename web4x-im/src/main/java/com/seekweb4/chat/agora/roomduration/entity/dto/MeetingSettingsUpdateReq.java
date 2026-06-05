package com.seekweb4.chat.agora.roomduration.entity.dto;

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
public class MeetingSettingsUpdateReq {
    
    /**
     * 会议室ID（roomId）
     */
    @NotBlank(message = "会议室ID不能为空")
    private String roomId;
    
    /**
     * 全员开麦
     */
    private Boolean allMic;
    
    /**
     * 全员禁言
     */
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
