package com.seekweb4.chat.agora.bean.req.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;

/**
 * 群会议查询请求对象
 * 
 * <p>用于查询群内正在进行的会议信息。</p>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class GroupMeetingQueryReq {
    
    /**
     * 应用ID
     */
    // @NotBlank(message = "应用ID不能为空")
    private String appId;
    
    /**
     * 群ID
     */
    @NotBlank(message = "群ID不能为空")
    private String groupId;
    
    /**
     * 场景ID（可选，用于筛选特定场景的会议）
     */
    private String sceneId;
}
