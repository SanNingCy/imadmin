package com.seekweb4.chat.agora.roomadmin.bean;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 会议列表查询请求对象
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
@Data
public class RoomListQueryReq {

    /**
     * 页码，从1开始
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
     * 房间ID，精确匹配
     */
    private String roomId;

    /**
     * 群ID，精确匹配
     */
    private String groupId;

    /**
     * 状态列表，支持多状态筛选
     * 可选值：active, inactive, destroyed, pending_create
     */
    private List<String> status;

    /**
     * 开始时间戳（毫秒）
     */
    private Long startTime;

    /**
     * 结束时间戳（毫秒）
     */
    private Long endTime;

    /**
     * 房间名称，模糊匹配
     */
    private String roomName;

    /**
     * 创建者ID，精确匹配
     */
    private String ownerId;

    /**
     * 排序字段
     * 可选值：createTime, updateTime, lastActiveTime, roomId
     */
    private String orderBy = "createTime";

    /**
     * 排序方向
     * 可选值：asc, desc
     */
    private String orderDirection = "desc";

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 场景ID
     */
    private String sceneId;
}
