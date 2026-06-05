package com.seekweb4.chat.agora.bean.req.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;

/**
 * 房间用户操作请求对象
 * 
 * <p>用于用户加入/离开房间的请求参数。</p>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class RoomUserActionReq {
    
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
}
