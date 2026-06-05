package com.seekweb4.chat.agora.roomduration.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 会议分页响应DTO
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class MeetingPageResp {

    /**
     * 总记录数
     */
    private Long total;

    private Long count;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 会议列表
     */
    private List<Map<String, Object>> list;

    /**
     * 统计信息
     */
    private Map<String, Object> statistics;
}
