package com.seekweb4.chat.agora.roomduration.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 会议查询请求DTO
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class MeetingQueryReq {

    /**
     * 页码（从1开始）
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 10;

    /**
     * 会议ID
     */
    private String meetingId;

    /**
     * 群组ID
     */
    private String groupId;

    /**
     * 房间名称（模糊查询）
     */
    private String roomName;

    /**
     * 创建者ID
     */
    private String ownerId;

    /**
     * 会议状态（pending_create, active, inactive, destroyed）
     */
    private String status;

    /**
     * 开始时间（毫秒时间戳）
     */
    private Long startTime;

    /**
     * 结束时间（毫秒时间戳）
     */
    private Long endTime;

    /**
     * 创建时间开始（毫秒时间戳）
     */
    private Long createTimeStart;

    /**
     * 创建时间结束（毫秒时间戳）
     */
    private Long createTimeEnd;

    /**
     * 排序字段
     */
    private String orderBy = "createTime";

    /**
     * 排序方向（asc, desc）
     */
    private String orderDirection = "desc";
}