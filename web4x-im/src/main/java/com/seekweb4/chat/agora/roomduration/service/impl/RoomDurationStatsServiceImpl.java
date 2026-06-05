package com.seekweb4.chat.agora.roomduration.service.impl;

import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingDetailQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingPageResp;
import com.seekweb4.chat.agora.roomduration.service.IMeetingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于 RoomListV2Entity 的会议统计服务实现
 * 替代原有的基于 MeetingEntity 的统计功能
 */
@Slf4j
@Service("roomDurationStatsService")
public class RoomDurationStatsServiceImpl implements IMeetingStatsService {

    @Resource
    private RoomListV2Repository roomListV2Repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Map<String, Object> getMeetingDurationStats(String startTime, String endTime, String dimension) throws Exception {
        log.info("获取平台总会议时长统计，startTime：{}，endTime：{}，dimension：{}", startTime, endTime, dimension);

        // 构建查询条件
        Criteria criteria = new Criteria();
        
        // 时间范围过滤
        if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
            try {
                Date start = SIMPLE_DATE_FORMAT.parse(startTime);
                Date end = SIMPLE_DATE_FORMAT.parse(endTime);
                // 结束时间加一天，包含整天
                Calendar cal = Calendar.getInstance();
                cal.setTime(end);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                end = cal.getTime();
                
                criteria.and("createTime").gte(start.getTime()).lt(end.getTime());
            } catch (ParseException e) {
                throw new IllegalArgumentException("时间格式错误，请使用 yyyy-MM-dd 格式");
            }
        }

        // 聚合查询
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(criteria),
            Aggregation.group()
                .sum("meetingTime").as("totalDuration")
                .count().as("totalMeetings")
                .addToSet("status").as("statuses")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, 
            "uikit_room_list_v2", 
            Map.class
        );

        Map<String, Object> result = new HashMap<>();
        if (results.getMappedResults().isEmpty()) {
            result.put("totalDuration", 0);
            result.put("totalMeetings", 0);
            result.put("statuses", new ArrayList<>());
        } else {
            Map<String, Object> data = results.getMappedResults().get(0);
            result.put("totalDuration", data.get("totalDuration"));
            result.put("totalMeetings", data.get("totalMeetings"));
            result.put("statuses", data.get("statuses"));
        }

        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("dimension", dimension);
        result.put("queryTime", System.currentTimeMillis());

        return result;
    }

    @Override
    public MeetingPageResp pageQuery(MeetingQueryReq req) throws Exception {
        log.info("分页查询会议列表，查询条件：{}", req);

        // 构建查询条件
        Criteria criteria = new Criteria();
        
        if (StringUtils.hasText(req.getGroupId())) {
            criteria.and("groupId").is(req.getGroupId());
        }
        if (StringUtils.hasText(req.getOwnerId())) {
            criteria.and("ownerId").is(req.getOwnerId());
        }
        if (StringUtils.hasText(req.getStatus())) {
            criteria.and("status").is(req.getStatus());
        }
        if (StringUtils.hasText(req.getRoomName())) {
            criteria.and("roomName").regex(req.getRoomName(), "i");
        }

        // 时间范围过滤
        if (req.getStartTime() != null && req.getEndTime() != null) {
            criteria.and("createTime").gte(req.getStartTime()).lt(req.getEndTime());
        }

        // 分页和排序
        int pageNum = req.getPageNum() != null ? req.getPageNum() : 1;
        int pageSize = req.getPageSize() != null ? req.getPageSize() : 10;
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        // 执行查询
        Query query = new Query(criteria).with(pageable);
        List<RoomListV2Entity> rooms = mongoTemplate.find(query, RoomListV2Entity.class);
        long total = mongoTemplate.count(query, RoomListV2Entity.class);

        // 转换为响应格式
        List<Map<String, Object>> meetingList = rooms.stream()
            .map(this::convertRoomToMap)
            .collect(Collectors.toList());

        // 构建分页响应
        MeetingPageResp resp = new MeetingPageResp();
        resp.setCount(total);
//        resp.setTotal(total);
//        resp.setPageNum(pageNum);
//        resp.setPageSize(pageSize);
//        resp.setPages((int) Math.ceil((double) total / pageSize));
        resp.setList(meetingList);

        // 添加统计信息
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalMeetings", total);
        statistics.put("queryTime", System.currentTimeMillis());
        
        // 状态统计
        Map<String, Long> statusStats = new HashMap<>();
        for (RoomListV2Entity room : rooms) {
            String status = room.getStatus() != null ? room.getStatus() : "unknown";
            statusStats.put(status, statusStats.getOrDefault(status, 0L) + 1);
        }
        statistics.put("statusStats", statusStats);
        
        // 总时长统计
        int totalDuration = rooms.stream()
            .mapToInt(room -> room.getMeetingTime() != null ? room.getMeetingTime() : 0)
            .sum();
        statistics.put("totalDuration", totalDuration);
        statistics.put("avgDuration", rooms.isEmpty() ? 0.0 : (double) totalDuration / rooms.size());
        
        resp.setStatistics(statistics);

        return resp;
    }

    @Override
    public Map<String, Object> getMeetingStatistics(MeetingQueryReq req) throws Exception {
        log.info("获取会议统计信息，查询条件：{}", req);

        // 构建查询条件
        Criteria criteria = new Criteria();
        
        if (StringUtils.hasText(req.getGroupId())) {
            criteria.and("groupId").is(req.getGroupId());
        }
        if (StringUtils.hasText(req.getOwnerId())) {
            criteria.and("ownerId").is(req.getOwnerId());
        }
        if (StringUtils.hasText(req.getStatus())) {
            criteria.and("status").is(req.getStatus());
        }

        // 时间范围过滤
        if (req.getStartTime() != null && req.getEndTime() != null) {
            criteria.and("createTime").gte(req.getStartTime()).lt(req.getEndTime());
        }

        // 聚合统计
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(criteria),
            Aggregation.group()
                .sum("meetingTime").as("totalDuration")
                .count().as("totalMeetings")
                .addToSet("status").as("statuses")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, 
            "uikit_room_list_v2", 
            Map.class
        );

        Map<String, Object> result = new HashMap<>();
        if (results.getMappedResults().isEmpty()) {
            result.put("totalDuration", 0);
            result.put("totalMeetings", 0);
            result.put("statuses", new ArrayList<>());
        } else {
            Map<String, Object> data = results.getMappedResults().get(0);
            result.put("totalDuration", data.get("totalDuration"));
            result.put("totalMeetings", data.get("totalMeetings"));
            result.put("statuses", data.get("statuses"));
        }

        result.put("queryTime", System.currentTimeMillis());
        return result;
    }

    @Override
    public Map<String, Object> getUserMeetingStats(String ownerId, String startTime, String endTime) throws Exception {
        log.info("查询用户 {} 会议统计，时间范围：{} - {}", ownerId, startTime, endTime);

        if (!StringUtils.hasText(ownerId)) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 构建查询条件
        Criteria criteria = new Criteria().and("ownerId").is(ownerId);
        
        if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
            try {
                Date start = SIMPLE_DATE_FORMAT.parse(startTime);
                Date end = SIMPLE_DATE_FORMAT.parse(endTime);
                Calendar cal = Calendar.getInstance();
                cal.setTime(end);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                end = cal.getTime();
                
                criteria.and("createTime").gte(start.getTime()).lt(end.getTime());
            } catch (ParseException e) {
                throw new IllegalArgumentException("时间格式错误，请使用 yyyy-MM-dd 格式");
            }
        }

        // 聚合统计
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(criteria),
            Aggregation.group()
                .sum("meetingTime").as("totalDuration")
                .count().as("totalMeetings")
                .addToSet("status").as("statuses")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, 
            "uikit_room_list_v2", 
            Map.class
        );

        Map<String, Object> result = new HashMap<>();
        if (results.getMappedResults().isEmpty()) {
            result.put("ownerId", ownerId);
            result.put("totalDuration", 0);
            result.put("totalMeetings", 0);
            result.put("statuses", new ArrayList<>());
        } else {
            Map<String, Object> data = results.getMappedResults().get(0);
            result.put("ownerId", ownerId);
            result.put("totalDuration", data.get("totalDuration"));
            result.put("totalMeetings", data.get("totalMeetings"));
            result.put("statuses", data.get("statuses"));
        }

        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("queryTime", System.currentTimeMillis());

        return result;
    }

    @Override
    public Map<String, Object> getStatusMeetingStats(String status, String startTime, String endTime) throws Exception {
        log.info("查询状态 {} 会议统计，时间范围：{} - {}", status, startTime, endTime);

        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException("会议状态不能为空");
        }

        // 构建查询条件
        Criteria criteria = new Criteria().and("status").is(status);
        
        if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
            try {
                Date start = SIMPLE_DATE_FORMAT.parse(startTime);
                Date end = SIMPLE_DATE_FORMAT.parse(endTime);
                Calendar cal = Calendar.getInstance();
                cal.setTime(end);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                end = cal.getTime();
                
                criteria.and("createTime").gte(start.getTime()).lt(end.getTime());
            } catch (ParseException e) {
                throw new IllegalArgumentException("时间格式错误，请使用 yyyy-MM-dd 格式");
            }
        }

        // 聚合统计
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(criteria),
            Aggregation.group()
                .sum("meetingTime").as("totalDuration")
                .count().as("totalMeetings")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, 
            "uikit_room_list_v2", 
            Map.class
        );

        Map<String, Object> result = new HashMap<>();
        if (results.getMappedResults().isEmpty()) {
            result.put("status", status);
            result.put("totalDuration", 0);
            result.put("totalMeetings", 0);
        } else {
            Map<String, Object> data = results.getMappedResults().get(0);
            result.put("status", status);
            result.put("totalDuration", data.get("totalDuration"));
            result.put("totalMeetings", data.get("totalMeetings"));
        }

        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("queryTime", System.currentTimeMillis());

        return result;
    }

    @Override
    public Map<String, Object> getMeetingDuration(MeetingDetailQueryReq req) {
        log.info("查询会议室详情，roomId：{}", req.getRoomId());

        if (!StringUtils.hasText(req.getRoomId())) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }

        Optional<RoomListV2Entity> roomOpt = roomListV2Repository.findByRoomId(req.getRoomId());
        if (!roomOpt.isPresent()) {
            throw new IllegalArgumentException("会议室不存在：" + req.getRoomId());
        }

        RoomListV2Entity room = roomOpt.get();
        return convertRoomToMap(room);
    }

    /**
     * 将 RoomListV2Entity 转换为 Map
     */
    private Map<String, Object> convertRoomToMap(RoomListV2Entity room) {
        Map<String, Object> data = new HashMap<>();
        
        try {
            // 使用反射获取所有字段
            Field[] fields = RoomListV2Entity.class.getDeclaredFields();
            
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                
                field.setAccessible(true);
                Object value = field.get(room);
                String fieldName = field.getName();
                data.put(fieldName, value);
            }
        } catch (Exception e) {
            log.error("反射转换RoomListV2Entity失败", e);
        }

        // 添加计算字段
        long now = System.currentTimeMillis();
        Long startTime = room.getStartTime();
        Long endTime = room.getEndTime();
        
        // 计算会议状态
        String meetingStatus;
        if (startTime == null) {
            meetingStatus = "closed";
        } else if (now < startTime) {
            meetingStatus = "pendingstart";
        } else if (now >= startTime && (endTime == null || now <= endTime)) {
            meetingStatus = "inprogress";
        } else {
            meetingStatus = "closed";
        }
        
        // 计算剩余时间
        long remaining = 0L;
        if (startTime != null && now < startTime) {
            long diff = startTime - now;
            remaining = diff / 1000L;
        }
        
        // 覆盖/添加特殊字段
        data.put("meetingId", room.getId());
        data.put("currentSize", room.getCurrentUsers());
        data.put("meetingStatus", meetingStatus);
        data.put("remainingTime", remaining);
        data.put("isNonPayPwd", Boolean.TRUE.equals(room.getIsNonPayPwd()));
        
        // 格式化时间字段为 yyyy-MM-dd HH:mm:ss 格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (startTime != null) {
            data.put("startTime", sdf.format(new Date(startTime)));
        } else {
            data.put("startTime", null);
        }
        if (endTime != null) {
            data.put("endTime", sdf.format(new Date(endTime)));
        } else {
            data.put("endTime", null);
        }
        if (room.getCreateTime() != null) {
            data.put("createTime", sdf.format(new Date(room.getCreateTime())));
        }
        if (room.getUpdateTime() != null) {
            data.put("updateTime", sdf.format(new Date(room.getUpdateTime())));
        }
        
        return data;
    }
}
