package com.seekweb4.chat.agora.bean.dto.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 群会议信息DTO
 * 
 * <p>用于返回群内正在进行的会议信息。</p>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class GroupMeetingInfoDto {
    
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 场景ID
     */
    private String sceneId;
    
    /**
     * 房间ID
     */
    private String roomId;
    
    /**
     * 群ID
     */
    private String groupId;
    
    /**
     * 房间状态
     * - active: 房间活跃状态，有用户在线
     * - inactive: 房间非活跃状态，无用户在线
     * - destroyed: 房间已销毁
     */
    private String status;
    
    /**
     * 房间负载数据
     */
    private Map<String, Object> payload;
    
    /**
     * 创建时间
     */
    private Long createTime;
    
    /**
     * 更新时间
     */
    private Long updateTime;
    
    /**
     * 最后活跃时间
     */
    private Long lastActiveTime;
    
    /**
     * 房间所有者ID
     */
    private String ownerId;
    
    /**
     * 聊天室ID
     */
    private String chatRoomId;
}
