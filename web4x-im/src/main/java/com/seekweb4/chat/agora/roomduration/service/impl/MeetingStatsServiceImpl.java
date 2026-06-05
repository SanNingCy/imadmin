package com.seekweb4.chat.agora.roomduration.service.impl;

import com.seekweb4.chat.agora.roomduration.entity.MeetingEntity;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingDetailQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingPageResp;
import com.seekweb4.chat.agora.roomduration.repository.MeetingRepository;
import com.seekweb4.chat.agora.roomduration.service.IMeetingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class MeetingStatsServiceImpl implements IMeetingStatsService {

    @Resource
    private MeetingRepository meetingRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Map<String, Object> getMeetingDurationStats(String startTime, String endTime, String dimension) throws Exception {
        // 解析时间范围
        long startMs = parseTimeToMillis(startTime, true);
        long endMs = parseTimeToMillis(endTime, false);
        
        log.info("查询会议统计，时间范围：{} - {} ({} - {})", startTime, endTime, startMs, endMs);
        
        // 查询指定时间范围内的会议
        List<MeetingEntity> meetings = meetingRepository.findByCreateTimeBetweenOrderByCreateTimeAsc(startMs, endMs);
        
        log.info("查询到会议数量：{}", meetings.size());
        for (MeetingEntity meeting : meetings) {
            log.info("会议：{}，创建时间：{}，会议时长：{}", meeting.getId(), meeting.getCreateTime(), meeting.getMeetingTime());
        }
        
        Map<String, Object> result = new HashMap<>();
        
        if ("month".equals(dimension)) {
            // 按月统计
            Map<String, Long> monthlyStats = calculateMonthlyStats(meetings);
            result.put("dimension", "month");
            result.put("stats", monthlyStats);
            result.put("totalDuration", monthlyStats.values().stream().mapToLong(Long::longValue).sum());
        } else {
            // 按日统计（默认）
            Map<String, Long> dailyStats = calculateDailyStats(meetings);
            result.put("dimension", "day");
            result.put("stats", dailyStats);
            result.put("totalDuration", dailyStats.values().stream().mapToLong(Long::longValue).sum());
        }
        
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("meetingCount", meetings.size());
        result.put("queryTime", System.currentTimeMillis());
        
        return result;
    }

    /**
     * 按日统计会议时长
     */
    private Map<String, Long> calculateDailyStats(List<MeetingEntity> meetings) {
        Map<String, Long> dailyStats = new TreeMap<>();
        
        for (MeetingEntity meeting : meetings) {
            String dateKey = formatMillisToDate(meeting.getCreateTime());
            long duration = calculateMeetingDuration(meeting);
            dailyStats.merge(dateKey, duration, Long::sum);
        }
        
        return dailyStats;
    }

    /**
     * 按月统计会议时长
     */
    private Map<String, Long> calculateMonthlyStats(List<MeetingEntity> meetings) {
        Map<String, Long> monthlyStats = new TreeMap<>();
        
        for (MeetingEntity meeting : meetings) {
            String monthKey = formatMillisToMonth(meeting.getCreateTime());
            long duration = calculateMeetingDuration(meeting);
            monthlyStats.merge(monthKey, duration, Long::sum);
        }
        
        return monthlyStats;
    }

    /**
     * 计算会议时长（分钟）
     */
    private long calculateMeetingDuration(MeetingEntity meeting) {
        if (meeting.getMeetingTime() != null) {
            return meeting.getMeetingTime();
        }
        return 0L;
    }

    /**
     * 解析时间字符串为毫秒时间戳
     */
    private long parseTimeToMillis(String timeStr, boolean isStart) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            if (isStart) {
                // 默认开始时间：30天前
                long defaultTime = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;
                log.info("使用默认开始时间：{}", formatMillisToDate(defaultTime));
                return defaultTime;
            } else {
                // 默认结束时间：当前时间
                long defaultTime = System.currentTimeMillis();
                log.info("使用默认结束时间：{}", formatMillisToDate(defaultTime));
                return defaultTime;
            }
        }
        
        try {
            LocalDate date = LocalDate.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime dateTime = isStart ? date.atStartOfDay() : date.atTime(23, 59, 59);
            long result = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            log.info("解析时间：{} -> {} ({})", timeStr, formatMillisToDate(result), result);
            return result;
        } catch (Exception e) {
            log.warn("解析时间失败：{}，使用默认时间", timeStr, e);
            if (isStart) {
                long defaultTime = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;
                log.info("使用默认开始时间：{}", formatMillisToDate(defaultTime));
                return defaultTime;
            } else {
                long defaultTime = System.currentTimeMillis();
                log.info("使用默认结束时间：{}", formatMillisToDate(defaultTime));
                return defaultTime;
            }
        }
    }

    /**
     * 格式化毫秒时间戳为日期字符串
     */
    private String formatMillisToDate(long millis) {
        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(millis), 
            ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 格式化毫秒时间戳为月份字符串
     */
    private String formatMillisToMonth(long millis) {
        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(millis), 
            ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    @Override
    public MeetingPageResp pageQuery(MeetingQueryReq req) throws Exception {
        log.info("开始分页查询会议，查询条件：{}", req);

        // 构建查询条件
        Query query = buildQuery(req);
        
        // 构建排序
        Sort sort = buildSort(req);
        query.with(sort);

        // 构建分页
        Pageable pageable = PageRequest.of(req.getPageNum() - 1, req.getPageSize());
        query.with(pageable);

        // 执行查询
        List<MeetingEntity> entities = mongoTemplate.find(query, MeetingEntity.class);
        
        // 查询总数
        Query countQuery = buildQuery(req);
        long total = mongoTemplate.count(countQuery, MeetingEntity.class);

        // 转换为响应对象
        List<Map<String, Object>> meetingList = new ArrayList<>();
        for (MeetingEntity entity : entities) {
            Map<String, Object> meetingInfo = convertMeetingEntityToMap(entity);
            
            // 添加计算字段
            long now = System.currentTimeMillis();
            String meetingStatus = calculateMeetingStatus(entity, now);
            long remainingTime = calculateRemainingTimeSeconds(entity, now);
            
            meetingInfo.put("meetingId", entity.getId());
            meetingInfo.put("meetingStatus", meetingStatus);
            meetingInfo.put("remainingTime", remainingTime);
            meetingInfo.put("isNonPayPwd", Boolean.TRUE.equals(entity.getIsNonPayPwd()));
            
            // 格式化时间字段
            meetingInfo.put("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(entity.getStartTime())));
            if (entity.getEndTime() != null) {
                meetingInfo.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(entity.getEndTime())));
            }
            meetingInfo.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(entity.getCreateTime())));
            meetingInfo.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(entity.getUpdateTime())));
            
            meetingList.add(meetingInfo);
        }

        // 构建响应
        MeetingPageResp response = new MeetingPageResp();
        response.setTotal(total);
        response.setPageNum(req.getPageNum());
        response.setPageSize(req.getPageSize());
        response.setPages((int) Math.ceil((double) total / req.getPageSize()));
        response.setList(meetingList);

        // 添加统计信息
        Map<String, Object> statistics = getMeetingStatistics(req);
        response.setStatistics(statistics);

        log.info("分页查询会议完成，总数：{}，当前页：{}，每页大小：{}", total, req.getPageNum(), req.getPageSize());
        return response;
    }

    @Override
    public Map<String, Object> getMeetingStatistics(MeetingQueryReq req) throws Exception {
        Map<String, Object> statistics = new HashMap<>();
        
        // 基础查询条件
        Query baseQuery = buildQuery(req);
        
        // 总会议数
        long totalMeetings = mongoTemplate.count(baseQuery, MeetingEntity.class);
        statistics.put("totalMeetings", totalMeetings);
        
        // 按状态统计
        Map<String, Long> statusStats = new HashMap<>();
        statusStats.put("pending_create", mongoTemplate.count(buildQueryWithStatus(req, "pending_create"), MeetingEntity.class));
        statusStats.put("active", mongoTemplate.count(buildQueryWithStatus(req, "active"), MeetingEntity.class));
        statusStats.put("inactive", mongoTemplate.count(buildQueryWithStatus(req, "inactive"), MeetingEntity.class));
        statusStats.put("destroyed", mongoTemplate.count(buildQueryWithStatus(req, "destroyed"), MeetingEntity.class));
        statistics.put("statusStats", statusStats);
        
        // 总会议时长
        List<MeetingEntity> allMeetings = mongoTemplate.find(baseQuery, MeetingEntity.class);
        long totalDuration = allMeetings.stream()
                .mapToLong(meeting -> meeting.getMeetingTime() != null ? meeting.getMeetingTime() : 0L)
                .sum();
        statistics.put("totalDuration", totalDuration);
        
        // 平均会议时长
        double avgDuration = totalMeetings > 0 ? (double) totalDuration / totalMeetings : 0.0;
        statistics.put("avgDuration", Math.round(avgDuration * 100.0) / 100.0);
        
        // 查询时间
        statistics.put("queryTime", System.currentTimeMillis());
        
        return statistics;
    }

    @Override
    public Map<String, Object> getUserMeetingStats(String ownerId, String startTime, String endTime) throws Exception {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 解析时间范围
        long startMs = parseTimeToMillis(startTime, true);
        long endMs = parseTimeToMillis(endTime, false);
        
        Map<String, Object> result = new HashMap<>();
        result.put("ownerId", ownerId);
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        
        // 用户总会议数
        long totalMeetings = meetingRepository.countByOwnerId(ownerId);
        result.put("totalMeetings", totalMeetings);
        
        // 时间范围内的会议数
        long meetingsInRange = meetingRepository.countByCreateTimeBetween(startMs, endMs);
        result.put("meetingsInRange", meetingsInRange);
        
        // 按状态统计
        Map<String, Long> statusStats = new HashMap<>();
        statusStats.put("pending_create", meetingRepository.countByStatusAndOwnerId("pending_create", ownerId));
        statusStats.put("active", meetingRepository.countByStatusAndOwnerId("active", ownerId));
        statusStats.put("inactive", meetingRepository.countByStatusAndOwnerId("inactive", ownerId));
        statusStats.put("destroyed", meetingRepository.countByStatusAndOwnerId("destroyed", ownerId));
        result.put("statusStats", statusStats);
        
        // 用户总会议时长
        List<MeetingEntity> userMeetings = mongoTemplate.find(
                Query.query(Criteria.where("ownerId").is(ownerId)), 
                MeetingEntity.class
        );
        long totalDuration = userMeetings.stream()
                .mapToLong(meeting -> meeting.getMeetingTime() != null ? meeting.getMeetingTime() : 0L)
                .sum();
        result.put("totalDuration", totalDuration);
        
        // 平均会议时长
        double avgDuration = totalMeetings > 0 ? (double) totalDuration / totalMeetings : 0.0;
        result.put("avgDuration", Math.round(avgDuration * 100.0) / 100.0);
        
        result.put("queryTime", System.currentTimeMillis());
        
        log.info("查询用户 {} 会议统计完成，总会议数：{}，总时长：{}", ownerId, totalMeetings, totalDuration);
        return result;
    }

    @Override
    public Map<String, Object> getStatusMeetingStats(String status, String startTime, String endTime) throws Exception {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("会议状态不能为空");
        }
        
        // 解析时间范围
        long startMs = parseTimeToMillis(startTime, true);
        long endMs = parseTimeToMillis(endTime, false);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        
        // 该状态的总会议数
        long totalMeetings = meetingRepository.countByStatus(status);
        result.put("totalMeetings", totalMeetings);
        
        // 时间范围内的会议数
        long meetingsInRange = meetingRepository.countByStatusAndCreateTimeBetween(status, startMs, endMs);
        result.put("meetingsInRange", meetingsInRange);
        
        // 该状态的会议列表
        List<MeetingEntity> statusMeetings = mongoTemplate.find(
                Query.query(Criteria.where("status").is(status)
                        .and("createTime").gte(startMs).lte(endMs)), 
                MeetingEntity.class
        );
        
        // 总会议时长
        long totalDuration = statusMeetings.stream()
                .mapToLong(meeting -> meeting.getMeetingTime() != null ? meeting.getMeetingTime() : 0L)
                .sum();
        result.put("totalDuration", totalDuration);
        
        // 平均会议时长
        double avgDuration = meetingsInRange > 0 ? (double) totalDuration / meetingsInRange : 0.0;
        result.put("avgDuration", Math.round(avgDuration * 100.0) / 100.0);
        
        result.put("queryTime", System.currentTimeMillis());
        
        log.info("查询状态 {} 会议统计完成，总会议数：{}，总时长：{}", status, totalMeetings, totalDuration);
        return result;
    }

    @Override
    public Map<String, Object> getMeetingDuration(MeetingDetailQueryReq req) {
        if (req.getRoomId() == null || req.getRoomId().trim().isEmpty()){
            throw new IllegalArgumentException("房间ID不能为空");
        }
        log.info("开始查询会议详情，roomId：{}", req.getRoomId());
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<MeetingEntity> meetingOpt = meetingRepository.findByRoomId(req.getRoomId());
            
            if (meetingOpt.isPresent()) {
                MeetingEntity meeting = meetingOpt.get();
                
                // 计算会议时长（分钟）
                long durationMinutes = 0;
                if (meeting.getStartTime() != null && meeting.getEndTime() != null) {
                    durationMinutes = (meeting.getEndTime() - meeting.getStartTime()) / (1000 * 60);
                } else if (meeting.getStartTime() != null && meeting.getEndTime() == null) {
                    // 如果会议还在进行中，计算到当前时间的时长
                    durationMinutes = (System.currentTimeMillis() - meeting.getStartTime()) / (1000 * 60);
                }
                
                // 时间格式化
                String startTimeStr = meeting.getStartTime() != null ? 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(meeting.getStartTime())) : null;
                String endTimeStr = meeting.getEndTime() != null ? 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(meeting.getEndTime())) : null;
                String createTimeStr = meeting.getCreateTime() != null ? 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(meeting.getCreateTime())) : null;
                String updateTimeStr = meeting.getUpdateTime() != null ? 
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(meeting.getUpdateTime())) : null;
                String queryTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                
                // 构建返回结果
                result.put("meetingId", meeting.getId());
                result.put("roomId", meeting.getRoomId());
                result.put("groupId", meeting.getGroupId());
                result.put("roomName", meeting.getRoomName());
                result.put("ownerId", meeting.getOwnerId());
                result.put("status", meeting.getStatus());
                
                // 原始时间戳（毫秒）
                result.put("startTime", startTimeStr);
                result.put("endTime", endTimeStr);
                result.put("createTime", createTimeStr);
                result.put("updateTime", updateTimeStr);
                result.put("queryTime", queryTimeStr);
                
                result.put("durationMinutes", durationMinutes);
                result.put("meetingMaxUsers", meeting.getMeetingMaxUsers());
                result.put("meetingTime", meeting.getMeetingTime());
                result.put("currentUsers", meeting.getCurrentUsers());
                result.put("allMic", meeting.getAllMic());
                result.put("allMute", meeting.getAllMute());
                result.put("isNonPayPwd", meeting.getIsNonPayPwd());
                result.put("timeZone", meeting.getTimeZone());
                result.put("success", true);
                
                log.info("查询会议详情成功，roomId：{}，会议时长：{}分钟", req.getRoomId(), durationMinutes);
            } else {
                result.put("success", false);
                result.put("msg", "未找到指定的会议室");
                result.put("roomId", req.getRoomId());
                log.warn("未找到会议室，roomId：{}", req.getRoomId());
            }
            
        } catch (Exception e) {
            log.error("查询会议详情失败，roomId：{}", req.getRoomId(), e);
            result.put("success", false);
            result.put("msg", "查询失败：" + e.getMessage());
            result.put("roomId", req.getRoomId());
        }
        
        return result;
    }

    /**
     * 构建查询条件
     */
    private Query buildQuery(MeetingQueryReq req) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        // 会议ID精确匹配
        if (req.getMeetingId() != null && !req.getMeetingId().trim().isEmpty()) {
            criteria.and("id").is(req.getMeetingId());
        }

        // 群组ID精确匹配
        if (req.getGroupId() != null && !req.getGroupId().trim().isEmpty()) {
            criteria.and("groupId").is(req.getGroupId());
        }

        // 房间名称模糊匹配
        if (req.getRoomName() != null && !req.getRoomName().trim().isEmpty()) {
            criteria.and("roomName").regex(req.getRoomName(), "i");
        }

        // 创建者ID精确匹配
        if (req.getOwnerId() != null && !req.getOwnerId().trim().isEmpty()) {
            criteria.and("ownerId").is(req.getOwnerId());
        }

        // 状态筛选
        if (req.getStatus() != null && !req.getStatus().trim().isEmpty()) {
            criteria.and("status").is(req.getStatus());
        }

        // 会议时间范围筛选
        if (req.getStartTime() != null) {
            criteria.and("startTime").gte(req.getStartTime());
        }
        if (req.getEndTime() != null) {
            criteria.and("startTime").lte(req.getEndTime());
        }

        // 创建时间范围筛选
        if (req.getCreateTimeStart() != null) {
            criteria.and("createTime").gte(req.getCreateTimeStart());
        }
        if (req.getCreateTimeEnd() != null) {
            criteria.and("createTime").lte(req.getCreateTimeEnd());
        }

        query.addCriteria(criteria);
        return query;
    }

    /**
     * 构建带状态的查询条件
     */
    private Query buildQueryWithStatus(MeetingQueryReq req, String status) {
        Query query = buildQuery(req);
        query.addCriteria(Criteria.where("status").is(status));
        return query;
    }

    /**
     * 构建排序
     */
    private Sort buildSort(MeetingQueryReq req) {
        String orderBy = req.getOrderBy();
        String orderDirection = req.getOrderDirection();

        Sort.Direction direction = "desc".equalsIgnoreCase(orderDirection) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;

        switch (orderBy) {
            case "updateTime":
                return Sort.by(direction, "updateTime");
            case "startTime":
                return Sort.by(direction, "startTime");
            case "roomName":
                return Sort.by(direction, "roomName");
            case "ownerId":
                return Sort.by(direction, "ownerId");
            case "status":
                return Sort.by(direction, "status");
            case "createTime":
            default:
                return Sort.by(direction, "createTime");
        }
    }

    /**
     * 使用反射将MeetingEntity转换为Map
     */
    private Map<String, Object> convertMeetingEntityToMap(MeetingEntity entity) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            java.lang.reflect.Field[] fields = MeetingEntity.class.getDeclaredFields();
            
            for (java.lang.reflect.Field field : fields) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || 
                    java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                
                field.setAccessible(true);
                Object value = field.get(entity);
                result.put(field.getName(), value);
            }
        } catch (Exception e) {
            log.error("反射转换MeetingEntity失败", e);
        }
        
        return result;
    }

    /**
     * 计算会议状态
     */
    private String calculateMeetingStatus(MeetingEntity meeting, long now) {
        if (now < meeting.getStartTime()) {
            return "pendingstart";
        } else if (now >= meeting.getStartTime() && (meeting.getEndTime() == null || now <= meeting.getEndTime())) {
            return "inprogress";
        } else {
            return "closed";
        }
    }

    /**
     * 计算剩余时间
     */
    private long calculateRemainingTimeSeconds(MeetingEntity meeting, long now) {
        if (now < meeting.getStartTime()) {
            long diff = meeting.getStartTime() - now;
            return diff / 1000L;
        }
        return 0L;
    }
}
