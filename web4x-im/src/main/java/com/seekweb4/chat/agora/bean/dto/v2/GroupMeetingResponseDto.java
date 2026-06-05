package com.seekweb4.chat.agora.bean.dto.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 群会议响应DTO
 * 
 * <p>用于返回群内会议查询结果。</p>
 * 
 * @author UIKit Team
 * @version 1.0
 * @since 1.0
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class GroupMeetingResponseDto {
    
    /**
     * 群ID
     */
    private String groupId;
    
    /**
     * 是否有正在进行的会议
     */
    private Boolean hasActiveMeeting;
    
    /**
     * 活跃会议数量
     */
    private Integer activeMeetingCount;
    
    /**
     * 群内会议列表（只包含活跃状态的会议）
     */
    private List<GroupMeetingInfoDto> activeMeetings;
}
