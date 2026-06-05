package com.seekweb4.chat.agora.bean.req.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 房间状态更新请求对象
 * 
 * <p>用于更新房间状态的请求参数。</p>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class RoomStatusUpdateReq {
    
    /**
     * 应用ID
     */
    @NotBlank(message = "应用ID不能为空")
    private String appId;
    
    /**
     * 场景ID
     */
    @NotBlank(message = "场景ID不能为空")
    private String sceneId;
    
    /**
     * 房间ID
     */
    @NotBlank(message = "房间ID不能为空")
    private String roomId;
    
    /**
     * 房间状态
     * - pending_create: 待创建
     * - active: 房间活跃状态，有用户在线
     * - inactive: 房间非活跃状态，无用户在线
     * - destroyed: 房间已销毁
     */
    @NotBlank(message = "房间状态不能为空")
    @Pattern(regexp = "^(pending_create|active|inactive|destroyed)$", message = "房间状态只能是pending_create、active、inactive或destroyed")
    private String status;
}
