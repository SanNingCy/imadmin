package com.seekweb4.chat.agora.roomduration.service;

import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingDetailQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingPageResp;

import java.util.Map;

/**
 * 会议统计服务接口
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
public interface IMeetingStatsService {
    
    /**
     * 获取平台总会议时长统计
     * 
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd）
     * @param dimension 统计维度（day=日，month=月）
     * @return 会议时长统计数据
     * @throws Exception 统计异常
     */
    Map<String, Object> getMeetingDurationStats(String startTime, String endTime, String dimension) throws Exception;

    /**
     * 分页查询会议列表
     * 
     * @param req 查询请求
     * @return 分页响应
     * @throws Exception 查询异常
     */
    MeetingPageResp pageQuery(MeetingQueryReq req) throws Exception;

    /**
     * 获取会议统计信息
     * 
     * @param req 查询请求
     * @return 统计信息
     * @throws Exception 统计异常
     */
    Map<String, Object> getMeetingStatistics(MeetingQueryReq req) throws Exception;

    /**
     * 根据用户查询会议统计
     * 
     * @param ownerId 用户ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 用户会议统计
     * @throws Exception 统计异常
     */
    Map<String, Object> getUserMeetingStats(String ownerId, String startTime, String endTime) throws Exception;

    /**
     * 根据会议状态查询统计
     * 
     * @param status 会议状态
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 状态统计
     * @throws Exception 统计异常
     */
    Map<String, Object> getStatusMeetingStats(String status, String startTime, String endTime) throws Exception;

    Map<String, Object> getMeetingDuration(MeetingDetailQueryReq req);
}
