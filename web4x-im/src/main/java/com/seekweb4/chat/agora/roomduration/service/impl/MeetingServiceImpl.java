package com.seekweb4.chat.agora.roomduration.service.impl;

import com.seekweb4.chat.agora.roomduration.entity.MeetingConfigV2Entity;
import com.seekweb4.chat.agora.roomduration.entity.MeetingEntity;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingCreateReq;
import com.seekweb4.chat.agora.roomduration.repository.MeetingRepository;
import com.seekweb4.chat.agora.roomduration.service.IMeetingService;
import com.seekweb4.chat.agora.roomduration.service.IMeetingConfigV2Service;
import com.seekweb4.chat.agora.roomduration.service.IUserBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class MeetingServiceImpl implements IMeetingService {

    @Resource
    private MeetingRepository meetingRepository;

    @Resource
    private IUserBalanceService userBalanceService;

    @Resource
    private IMeetingConfigV2Service meetingConfigV2Service;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DEFAULT_TIME_ZONE = "UTC+8";
    private static final boolean DEFAULT_ALL_MIC = true;
    private static final boolean DEFAULT_ALL_MUTE = false;

    @Override
    public Map<String, Object> createMeeting(MeetingCreateReq req) {
        long now = System.currentTimeMillis();
        // 默认值处理
        String effectiveTimeZone = (req.getTimeZone() == null || req.getTimeZone().trim().isEmpty())
                ? DEFAULT_TIME_ZONE
                : req.getTimeZone();
        Boolean effectiveAllMic = req.getAllMic() != null ? req.getAllMic() : DEFAULT_ALL_MIC;
        Boolean effectiveAllMute = req.getAllMute() != null ? req.getAllMute() : DEFAULT_ALL_MUTE;

        // 会议开始时间获取当前的时间
        Long startEpochMillis = now;
//        Long startEpochMillis = parseStartTime(req.getStartTime(), effectiveTimeZone);
        Long endEpochMillis = startEpochMillis + Integer.parseInt(req.getMeetingTime()) * 60_000L;

        // 开始时间校验：不可早于当前时间；不可超过30天后
//        long maxAhead = 30L * 24 * 60 * 60 * 1000; // 30天
//        if (startEpochMillis < now) {
//            throw new IllegalArgumentException("开始时间不能早于当前时间");
//        }
//        if (startEpochMillis - now > maxAhead) {
//            throw new IllegalArgumentException("开始时间不可超过30天");
//        }

//        if (req.getOwnerId() != null) {
//            long overlap = meetingRepository.countByOwnerIdAndStartTimeLessThanAndEndTimeGreaterThan(
//                    req.getOwnerId(), endEpochMillis, startEpochMillis);
//            if (overlap > 0) {
//                throw new IllegalArgumentException("创建失败：您已会议正在进行中");
//            }
//        }
        // 1) 检查用户是否有未销毁的会议（不基于时间，只基于状态）
        if (req.getOwnerId() != null) {
            long activeMeetings = meetingRepository.countByOwnerIdAndStatusNot(
                    req.getOwnerId(), "destroyed");
            if (activeMeetings > 0) {
                throw new IllegalArgumentException("您已有会议正在进行中，暂时无法创建新会议，请处理当前会议");
            }
        }
        // 2) 余额校验并扣费：按会议时长与人数计算后，直接扣减并记录日志
        if (req.getOwnerId() != null && req.getMeetingTime() != null && req.getMeetingMaxUsers() != null) {
            try {
                  Map<String, BigDecimal> calc = calculateTokens(req.getOwnerId(), req.getMeetingTime(), req.getMeetingMaxUsers());
                BigDecimal available = calc.get("availableTokens");
                BigDecimal need = calc.get("meetingTokens");
                if (available == null || need == null) {
                    throw new IllegalArgumentException("计算结果缺失");
                }
                if (available.compareTo(need) < 0) {
                    throw new IllegalArgumentException("您的Token不足");
                }
                String title = "会议创建扣费";
                String info = String.format("会议室：%s，时长：%s分钟，人数：%s人，消耗积分：%s", 
                        req.getRoomName(), req.getMeetingTime(), req.getMeetingMaxUsers(), need);
                Map<String, BigDecimal> deducted = deductTokens(req.getOwnerId(), req.getMeetingTime(), req.getMeetingMaxUsers(), title, info);
                BigDecimal deductedAmount = deducted.get("deductTokens");
                try {
                    userBalanceService.updateUserWithhold(req.getOwnerId(), deductedAmount);
                } catch (Exception e) {
                    // 更新withhold失败不应吞掉扣费结果，但需要告警
                    log.warn("更新withhold失败 userId={}, amount={}", req.getOwnerId(), deductedAmount, e);
                }
            } catch (IllegalArgumentException ex) {
                throw ex;
            } catch (Exception e) {
                throw new IllegalArgumentException("参数异常: " + e.getMessage());
            }
        }

        //  生成会议室ID
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf(ThreadLocalRandom.current().nextInt(10000, 100000));
        String roomId = "group_" + timestamp + "_" + random;

        Boolean noPwd = req.getIsNonPayPwd() != null ? req.getIsNonPayPwd() : Boolean.FALSE;
        
        // 查询用户的idno字段
        String userIdNo = null;
        if (req.getOwnerId() != null) {
            try {
                userIdNo = userBalanceService.getIdNoByUserId(req.getOwnerId());
                log.info("查询到用户idno，ownerId={}, idno={}", req.getOwnerId(), userIdNo);
            } catch (Exception e) {
                log.warn("查询用户idno失败，ownerId={}，将使用null值", req.getOwnerId(), e);
                // 查询失败不影响会议创建，继续执行
            }
        }
        String mtName = resolveMeetingTimeLabel(Integer.valueOf(req.getMeetingTime()));

        Integer minMeetingTime = getMinMeetingTimeFromConfig();
        String minMeetingTimeName = getMinMeetingTimeNameFromConfig();

        MeetingEntity entity = new MeetingEntity()
                .setRoomId(roomId)
                .setIdNo(userIdNo)  // 设置用户的idno字段
                .setGroupId(req.getGroupId())
                .setRoomName(req.getRoomName())
                .setOwnerId(req.getOwnerId())
                .setAllMic(effectiveAllMic)
                .setAllMute(effectiveAllMute)
                .setStartTime(startEpochMillis)
                .setTimeZone(effectiveTimeZone)
                .setMeetingMaxUsers(Integer.valueOf(req.getMeetingMaxUsers()))
                .setMeetingTime(Integer.valueOf(req.getMeetingTime()))
                .setCurrentUsers(1)
                .setIsNonPayPwd(noPwd)
                .setEndTime(endEpochMillis)
                .setStatus("active")
                .setMeetingTimeName(mtName)
                .setMinMeetingTime(minMeetingTime)
                .setMinMeetingTimeName(minMeetingTimeName)
                .setCreateTime(now)
                .setUpdateTime(now);

        meetingRepository.save(entity);
        log.info("Meeting saved. groupId={}, roomName={}, ownerId={}", req.getGroupId(), req.getRoomName(), req.getOwnerId());
        // 返回创建的会议数据
        return buildMeetingResponse(entity);
    }

    @Override
    public Map<String, BigDecimal> calculateTokens(String ownerId, String meetingTime, String meetingMaxUsers) throws Exception {
        if (ownerId == null || meetingTime == null || meetingMaxUsers == null) {
            throw new IllegalArgumentException("参数异常");
        }

        // 读取全局会议配置（meeting_config_v2）
        MeetingConfigV2Entity cfg = meetingConfigV2Service.getOrInit();
        if (cfg == null || cfg.getStepConsumptionToken() == null) {
            throw new IllegalArgumentException("参数异常: 缺少计费配置");
        }

        // 校验 meetingTime 是否在配置的 timeOline 中
        int minutes;
        try {
            minutes = Integer.parseInt(meetingTime);
        } catch (Exception e) {
            throw new IllegalArgumentException("参数异常: meetingTime");
        }
        boolean timeSupported = false;
        if (cfg.getTimeOline() != null) {
            for (java.util.Map<String, Object> t : cfg.getTimeOline()) {
                Object v = t.get("value");
                if (v != null && String.valueOf(v).equals(String.valueOf(minutes))) {
                    timeSupported = true;
                    break;
                }
            }
        }
        if (!timeSupported) {
            throw new IllegalArgumentException("参数异常: meetingTime");
        }

        // 校验 meetingMaxUsers 是否在配置的 userTierOptions 中，并给出系数
        int maxUsers;
        try {
            maxUsers = Integer.parseInt(meetingMaxUsers);
        } catch (Exception e) {
            throw new IllegalArgumentException("参数异常: meetingMaxUsers");
        }
        boolean tierSupported = false;
        if (cfg.getUserTierOptions() != null) {
            for (java.util.Map<String, Object> m : cfg.getUserTierOptions()) {
                Object v = m.get("value");
                if (v != null && String.valueOf(v).equals(String.valueOf(maxUsers))) {
                    tierSupported = true;
                    break;
                }
            }
        }
        if (!tierSupported) {
            throw new IllegalArgumentException("参数异常: meetingMaxUsers");
        }
        // 价格 = 每分钟消耗(stepConsumptionToken) * minutes * 人数
        BigDecimal perMinute = cfg.getStepConsumptionToken();
        BigDecimal meetingTokens = perMinute
                .multiply(new BigDecimal(minutes))
                .multiply(new BigDecimal(maxUsers))
                .setScale(3, RoundingMode.HALF_UP);

        // 余额与折扣价（暂不打折，折扣价=原价；且返回availableTokens为余额）
        BigDecimal available = userBalanceService.getUserBalance(ownerId).setScale(1, java.math.RoundingMode.HALF_UP);
        BigDecimal discountMeetingTokens = meetingTokens;

        Map<String, java.math.BigDecimal> result = new java.util.HashMap<>();
        result.put("availableTokens", available);
        result.put("meetingTokens", meetingTokens);
        result.put("discountMeetingTokens", discountMeetingTokens);
        return result;
    }

    /**
     * 解析 meetingTime 对应配置的 label，仅供创建返回时附加。
     */
    private String resolveMeetingTimeLabel(Integer meetingTimeMinutes) {
        if (meetingTimeMinutes == null) return null;
        MeetingConfigV2Entity cfg = null;
        try { cfg = meetingConfigV2Service.getOrInit(); } catch (Exception ignore) {}
        if (cfg == null || cfg.getTimeOline() == null) return null;
        for (java.util.Map<String, Object> item : cfg.getTimeOline()) {
            if (item == null) continue;
            Object value = item.get("value");
            if (value == null) continue;
            if (String.valueOf(value).equals(String.valueOf(meetingTimeMinutes))) {
                Object label = item.get("name");
                return label == null ? null : String.valueOf(label);
            }
        }
        return null;
    }

    /**
     * 获取时长配置中的最小时长（分钟）
     */
    private Integer getMinMeetingTimeFromConfig() {
        try {
            MeetingConfigV2Entity cfg = meetingConfigV2Service.getOrInit();
            if (cfg == null || cfg.getTimeOline() == null || cfg.getTimeOline().isEmpty()) {
                return null;
            }
            
            Integer minTime = null;
            for (java.util.Map<String, Object> item : cfg.getTimeOline()) {
                if (item == null) continue;
                Object value = item.get("value");
                if (value == null) continue;
                
                try {
                    Integer timeValue = Integer.valueOf(String.valueOf(value));
                    if (minTime == null || timeValue < minTime) {
                        minTime = timeValue;
                    }
                } catch (NumberFormatException e) {
                    // 忽略无法解析的值
                }
            }
            return minTime;
        } catch (Exception e) {
            // 忽略异常，返回null
            return null;
        }
    }

    /**
     * 获取时长配置中最小时长对应的名称
     */
    private String getMinMeetingTimeNameFromConfig() {
        try {
            MeetingConfigV2Entity cfg = meetingConfigV2Service.getOrInit();
            if (cfg == null || cfg.getTimeOline() == null || cfg.getTimeOline().isEmpty()) {
                return null;
            }
            
            Integer minTime = null;
            String minTimeName = null;
            
            for (java.util.Map<String, Object> item : cfg.getTimeOline()) {
                if (item == null) continue;
                Object value = item.get("value");
                Object name = item.get("name");
                if (value == null) continue;
                
                try {
                    Integer timeValue = Integer.valueOf(String.valueOf(value));
                    if (minTime == null || timeValue < minTime) {
                        minTime = timeValue;
                        minTimeName = name != null ? String.valueOf(name) : null;
                    }
                } catch (NumberFormatException e) {
                    // 忽略无法解析的值
                }
            }
            return minTimeName;
        } catch (Exception e) {
            // 忽略异常，返回null
            return null;
        }
    }
    @Override
    public Map<String, BigDecimal> deductTokens(String ownerId, String meetingTime, String meetingMaxUsers, String title, String info) throws Exception {
        Map<String, BigDecimal> calc = calculateTokens(ownerId, meetingTime, meetingMaxUsers);
        BigDecimal need = calc.get("meetingTokens");
        BigDecimal available = calc.get("availableTokens");
        if (need == null || available == null) {
            throw new IllegalArgumentException("参数异常: 计算结果缺失");
        }
        if (available.compareTo(need) < 0) {
            throw new IllegalArgumentException("扣费失败：Token不足");
        }
        boolean ok = userBalanceService.deductUserPoints(ownerId, need, title == null ? "会议扣费" : title, info == null ? "按分钟人数计费扣减" : info);
        if (!ok) {
            throw new IllegalStateException("扣费失败：余额扣减异常");
        }
        BigDecimal remain = userBalanceService.getUserBalance(ownerId).setScale(1, java.math.RoundingMode.HALF_UP);
        java.util.Map<String, BigDecimal> result = new java.util.HashMap<>();
        result.put("deductTokens", need);
        result.put("remainTokens", remain);
        return result;
    }

    @Override
    public Map<String, Object> getGroupCurrentMeeting(String groupId) {
        Map<String, Object> data = new HashMap<>();
        if (groupId == null || groupId.trim().isEmpty()) {
            return data;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // 查找群组内最新的有效会议（非销毁且未结束）
        MeetingEntity m = meetingRepository.findTopByGroupIdAndStatusNotAndEndTimeGreaterThanOrderByStartTimeDesc(groupId, "destroyed", currentTime);
        if (m == null) {
            return data;
        }
        
        // 使用优化的方法：自动映射基础字段 + 手动添加计算字段
        data = buildMeetingResponse(m);
        
        return data;
    }
    
    /**
     * 构建会议响应数据
     * 自动包含所有基础字段，同时支持添加计算字段
     * 当添加新字段时，只需要在MeetingEntity中添加，这里会自动包含
     */
    private Map<String, Object> buildMeetingResponse(MeetingEntity meeting) {
        Map<String, Object> data = new HashMap<>();
        
        // 1. 自动映射所有基础字段（使用反射）
        Map<String, Object> baseFields = convertMeetingEntityToMap(meeting);
        data.putAll(baseFields);
        
        // 2. 添加计算字段和特殊处理字段
        long now = System.currentTimeMillis();
        
        // 计算会议状态
        String meetingStatus;
        if (now < meeting.getStartTime()) {
            meetingStatus = "pendingstart";
        } else if (now >= meeting.getStartTime() && (meeting.getEndTime() == null || now <= meeting.getEndTime())) {
            meetingStatus = "inprogress";
        } else {
            meetingStatus = "closed";
        }
        
        // 房间活跃状态：pending_create/active/inactive/destroyed
        String roomStatus;
        if (now < meeting.getStartTime()) {
            roomStatus = "pending_create";
        } else if (meeting.getEndTime() != null && now > meeting.getEndTime()) {
            roomStatus = "destroyed";
        } else {
            Integer cs = meeting.getCurrentUsers();
            roomStatus = (cs != null && cs > 0) ? "active" : "inactive";
        }
        
        // 计算剩余时间
        long remaining;
        if (now < meeting.getStartTime()) {
            long diff = meeting.getStartTime() - now;
            remaining = diff / 1000L;
        } else {
            remaining = 0L;
        }
        
        // 3. 覆盖/添加特殊字段
        data.put("meetingId", meeting.getRoomId());  // 确保字段名正确
//        data.put("meetingId", meeting.getId());  // 确保字段名正确
        data.put("currentSize", meeting.getCurrentUsers());  // 字段名映射
        data.put("meetingStatus", meetingStatus);  // 计算字段
        data.put("remainingTime", remaining);  // 计算字段（未开始倒计时，开始后为"0"）
        data.put("isNonPayPwd", Boolean.TRUE.equals(meeting.getIsNonPayPwd()));  // 特殊处理
        data.put("startTime", meeting.getStartTime());  // 格式化
        
        return data;
    }
    
    /**
     * 使用反射将MeetingEntity转换为Map，自动包含所有字段
     * 这样当添加新字段时，不需要手动修改这个方法
     */
    private Map<String, Object> convertMeetingEntityToMap(MeetingEntity entity) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取所有字段
            Field[] fields = MeetingEntity.class.getDeclaredFields();
            
            for (Field field : fields) {
                // 跳过静态字段和final字段
                if (Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                
                field.setAccessible(true);
                Object value = field.get(entity);
                
                // 使用原始字段名
                String fieldName = field.getName();
                result.put(fieldName, value);
            }
        } catch (Exception e) {
            log.error("反射转换MeetingEntity失败", e);
            // 如果反射失败，回退到手动映射
            return convertMeetingEntityToMapManually(entity);
        }
        
        return result;
    }
    
    /**
     * 手动映射方法（作为反射失败时的备选方案）
     */
    private Map<String, Object> convertMeetingEntityToMapManually(MeetingEntity entity) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", entity.getId());
        result.put("groupId", entity.getGroupId());
        result.put("roomName", entity.getRoomName());
        result.put("ownerId", entity.getOwnerId());
        result.put("allMic", entity.getAllMic());
        result.put("allMute", entity.getAllMute());
        result.put("startTime", entity.getStartTime());
        result.put("timeZone", entity.getTimeZone());
        result.put("meetingMaxUsers", entity.getMeetingMaxUsers());
        result.put("meetingTime", entity.getMeetingTime());
        result.put("currentUsers", entity.getCurrentUsers());
        result.put("endTime", entity.getEndTime());
        result.put("isNonPayPwd", entity.getIsNonPayPwd());
        result.put("status", entity.getStatus());
        result.put("createTime", entity.getCreateTime());
        result.put("updateTime", entity.getUpdateTime());
        return result;
    }
    
    /**
     * 构建会议列表响应数据
     * 用于列表查询，包含所有基础字段和计算字段
     */
    private Map<String, Object> buildMeetingListResponse(MeetingEntity meeting, long now) {
        Map<String, Object> data = new HashMap<>();
        
        // 1. 自动映射所有基础字段（使用反射）
        Map<String, Object> baseFields = convertMeetingEntityToMap(meeting);
        data.putAll(baseFields);
        
        // 2. 添加计算字段和特殊处理字段
        String meetingStatus;
        if (now < meeting.getStartTime()) {
            meetingStatus = "pendingstart";
        } else if (now >= meeting.getStartTime() && (meeting.getEndTime() == null || now <= meeting.getEndTime())) {
            meetingStatus = "inprogress";
        } else {
            meetingStatus = "closed";
        }
        
        // 计算剩余时间（未开始时到开始的秒数，开始后为0）
        long remaining;
        if (now < meeting.getStartTime()) {
            long diff = meeting.getStartTime() - now;
            remaining = diff / 1000L;
        } else {
            remaining = 0L;
        }
        
        // 3. 覆盖/添加特殊字段
        data.put("meetingId", meeting.getRoomId());  // 确保字段名正确
//        data.put("meetingId", meeting.getId());  // 确保字段名正确
        data.put("currentUsers", meeting.getCurrentUsers());  // 保持原始字段名
        data.put("meetingStatus", meetingStatus);  // 计算字段
        data.put("remainingTime", remaining);  // 计算字段
        data.put("isNonPayPwd", Boolean.TRUE.equals(meeting.getIsNonPayPwd()));  // 特殊处理
        
        // 格式化时间字段
        data.put("startTime", meeting.getStartTime());
        if (meeting.getEndTime() != null) {
            data.put("endTime", meeting.getEndTime());
        }
//        data.put("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(meeting.getStartTime())));
//        if (meeting.getEndTime() != null) {
//            data.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(meeting.getEndTime())));
//        }
        
        return data;
    }

    

    @Override
    public boolean addGroupTime(String groupId, String ownerId, String meetingTime, String roomId) throws Exception {
        if (ownerId == null || meetingTime == null) {
            throw new IllegalArgumentException("参数异常：ownerId和meetingTime为必传参数");
        }
        
        MeetingEntity m;
        if (roomId != null && !roomId.trim().isEmpty()) {
            // 如果提供了roomId，优先通过roomId查找会议
            Optional<MeetingEntity> meetingOpt = meetingRepository.findByRoomIdAndStatusNot(roomId,"destroyed");
            if (!meetingOpt.isPresent()) {
                throw new IllegalArgumentException("会议不存在：" + roomId);
            }
            m = meetingOpt.get();
            // 如果提供了groupId，验证会议是否属于指定群组
            if (groupId != null && !groupId.trim().isEmpty() && !groupId.equals(m.getGroupId())) {
                throw new IllegalArgumentException("会议不属于指定群组");
            }
        } else {
            // 如果没有提供roomId，需要根据groupId查找会议
            if (groupId == null || groupId.trim().isEmpty()) {
                throw new IllegalArgumentException("参数异常：当roomId为空时，groupId为必传参数");
            }
            // 找到该群最新的一场会议
            m = meetingRepository.findTopByGroupIdOrderByStartTimeDesc(groupId);
            if (m == null) {
                return false;
            }
        }
        // 检查是否开启免密支付
        if (!Boolean.TRUE.equals(m.getIsNonPayPwd())) {
            throw new IllegalArgumentException("暂未开启免密支付不支持该操作！");
        }
        // 计算需要的代币（按新增分钟数与当前人数）
        // 这里以该会议当前最大人数作为基数，或可替换为 currentUsers
        Map<String, BigDecimal> calc = calculateTokens(ownerId, meetingTime, String.valueOf(m.getMeetingMaxUsers()));
        BigDecimal need = calc.get("meetingTokens");
        BigDecimal available = calc.get("availableTokens");
        if (available.compareTo(need) < 0) {
            throw new IllegalArgumentException("创建失败: 您的Token不足");
        }
        // 扣费
        String info = String.format("会议室：%s，加时：%s分钟，人数：%s人，消耗积分：%s", 
                m.getRoomName(), meetingTime, m.getMeetingMaxUsers(), need);
        Map<String, BigDecimal> deducted = deductTokens(ownerId, meetingTime, String.valueOf(m.getMeetingMaxUsers()), "会议加时扣费", info);
        // 累加withhold字段
        BigDecimal deductAmount = deducted.get("deductTokens");
        userBalanceService.addUserWithhold(ownerId, deductAmount);
        // 更新结束时间
        int addMinutes = Integer.parseInt(meetingTime);
        long addMillis = addMinutes * 60_000L;
        Long base = m.getEndTime() != null ? m.getEndTime() : m.getStartTime();
        m.setEndTime(base + addMillis);
        m.setUpdateTime(System.currentTimeMillis());
        meetingRepository.save(m);
        return true;
    }

    @Override
    public Map<String, Object> getGroupActiveMeetings(String groupId) throws Exception {
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new IllegalArgumentException("群ID不能为空");
        }
        
        long now = System.currentTimeMillis();
        // 查找群内所有未结束的会议（包括待开始、进行中、未销毁的会议）
        List<MeetingEntity> activeMeetings = meetingRepository.findByGroupIdAndStartTimeLessThanAndEndTimeGreaterThan(
                groupId, now + 1, now - 1);
        
        List<Map<String, Object>> meetingList = new ArrayList<>();
        for (MeetingEntity meeting : activeMeetings) {
            // 使用统一的方法构建会议信息
            Map<String, Object> meetingInfo = buildMeetingListResponse(meeting, now);
            meetingList.add(meetingInfo);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("groupId", groupId);
        result.put("hasActiveMeeting", !meetingList.isEmpty());
        result.put("activeMeetingCount", meetingList.size());
        result.put("activeMeetings", meetingList);
        
        return result;
    }

    @Override
    public Map<String, Object> getGroupActiveMeetingsByStatus(String groupId) throws Exception {
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new IllegalArgumentException("群ID不能为空");
        }
        
        // 查询群内活跃状态的会议（只查询active和inactive状态）
        List<MeetingEntity> activeMeetings = new ArrayList<>();
        List<MeetingEntity> activeList = meetingRepository.findByGroupIdAndStatus(groupId, "active");
        List<MeetingEntity> inactiveList = meetingRepository.findByGroupIdAndStatus(groupId, "inactive");
        activeMeetings.addAll(activeList);
        activeMeetings.addAll(inactiveList);
        
        List<Map<String, Object>> meetingList = new ArrayList<>();
        for (MeetingEntity meeting : activeMeetings) {
            // 使用统一的方法构建会议信息
            Map<String, Object> meetingInfo = buildMeetingListResponse(meeting, System.currentTimeMillis());
            meetingList.add(meetingInfo);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("groupId", groupId);
        result.put("hasActiveMeeting", !meetingList.isEmpty());
        result.put("activeMeetingCount", meetingList.size());
        result.put("activeMeetings", meetingList);
        
        return result;
    }

    @Override
    public boolean updateMeetingSettings(com.seekweb4.chat.agora.roomduration.entity.dto.MeetingSettingsUpdateReq req) throws Exception {
        log.info("updateMeetingSettings, roomId: {}, allMic: {}, allMute: {}, status: {}", 
                req.getRoomId(), req.getAllMic(), req.getAllMute(), req.getStatus());
        
        if (req.getRoomId() == null || req.getRoomId().trim().isEmpty()) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }
        
        // 查询会议室
        MeetingEntity meeting = meetingRepository.findByRoomId(req.getRoomId()).orElse(null);
        if (meeting == null) {
            if ("destroyed".equals(req.getStatus())) {
                log.info("销毁成功：roomId: {}", req.getRoomId());
                return true;
            }
            throw new RuntimeException("会议室不存在");
        }
        
        // 仅在前端传入对应字段时才更新，未传的不改动
        if (req.getAllMic() != null) {
            meeting.setAllMic(req.getAllMic());
        }
        if (req.getAllMute() != null) {
            meeting.setAllMute(req.getAllMute());
        }
        
        // 如果传入了状态，则更新状态
        if (req.getStatus() != null && !req.getStatus().trim().isEmpty()) {
            // 验证状态转换合法性
            if (!isValidStatusTransition(meeting.getStatus(), req.getStatus())) {
                throw new IllegalArgumentException("状态转换不合法：从 " + meeting.getStatus() + " 到 " + req.getStatus());
            }
            meeting.setStatus(req.getStatus());
        }
        
        // 更新修改时间
        meeting.setUpdateTime(System.currentTimeMillis());
        
        // 保存到数据库
        meetingRepository.save(meeting);
        
        log.info("updateMeetingSettings, success, roomId: {}, status: {}", req.getRoomId(), meeting.getStatus());
        return true;
    }

    @Override
    public Map<String, Object> getMeetingByRoomId(String roomId) throws Exception {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }
        
        // 根据roomId查找会议
        Optional<MeetingEntity> meetingOpt = meetingRepository.findByRoomId(roomId);
        if (!meetingOpt.isPresent()) {
            throw new IllegalArgumentException("会议室不存在：" + roomId);
        }
        
        MeetingEntity meeting = meetingOpt.get();
        
        // 使用现有的buildMeetingResponse方法构建响应数据
        return buildMeetingResponse(meeting);
    }

    /**
     * 验证状态转换是否合法
     * 
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @return 是否合法
     */
    private boolean isValidStatusTransition(String oldStatus, String newStatus) {
        if (oldStatus == null || newStatus == null) {
            return false;
        }
        
        // 相同状态转换总是合法的
        if (oldStatus.equals(newStatus)) {
            return true;
        }
        
        // 已销毁的房间不能转换到其他状态
        if ("destroyed".equals(oldStatus)) {
            return false;
        }
        
        // 任何状态都可以转换为destroyed
        if ("destroyed".equals(newStatus)) {
            return true;
        }
        
        // 其他状态转换规则
        switch (oldStatus) {
            case "pending_create":
                return "active".equals(newStatus) || "inactive".equals(newStatus) || "destroyed".equals(newStatus);
            case "inactive":
                return "active".equals(newStatus) || "destroyed".equals(newStatus);
            case "active":
                return "inactive".equals(newStatus) || "destroyed".equals(newStatus);
            default:
                return false;
        }
    }

    private Long parseStartTime(String startTime, String timeZone) {
        if (startTime == null || startTime.trim().isEmpty()) {
            throw new IllegalArgumentException("参数异常: startTime为空");
        }
        String s = startTime.trim();
        // 若为纯数字，则按时间戳处理（支持秒/毫秒）
        if (s.matches("^\\d+$")) {
            try {
                long ts = Long.parseLong(s);
                if (ts < 1_000_000_000_000L) { // 10位秒级时间戳
                    ts = ts * 1000L;
                }
                return ts;
            } catch (Exception e) {
                throw new IllegalArgumentException("参数异常: startTime时间戳格式错误");
            }
        }
        // 否则按 yyyy-MM-dd HH:mm:ss 结合时区解析
        try {
            ZoneId zoneId = parseZoneId(timeZone);
            LocalDateTime localDateTime = LocalDateTime.parse(s, DATE_TIME_FORMATTER);
            return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
        } catch (Exception e) {
            throw new IllegalArgumentException("参数异常: startTime日期格式错误");
        }
    }

    private ZoneId parseZoneId(String timeZone) {
        // 简单处理形如 UTC+8 / UTC-5
        try {
            String tz = timeZone.trim().toUpperCase();
            if (tz.startsWith("UTC")) {
                String offset = tz.substring(3).trim();
                if (offset.isEmpty()) {
                    return ZoneId.of("UTC");
                }
                if (!offset.startsWith("+") && !offset.startsWith("-")) {
                    offset = "+" + offset;
                }
                // 将 +8 转为 +08:00
                if (!offset.contains(":")) {
                    int sign = offset.startsWith("-") ? -1 : 1;
                    String num = offset.substring(1);
                    int hours = Integer.parseInt(num);
                    String hh = String.format("%02d", Math.abs(hours));
                    offset = (sign < 0 ? "-" : "+") + hh + ":00";
                }
                return ZoneId.of("UTC" + offset);
            }
            return ZoneId.of(tz);
        } catch (Exception e) {
            return ZoneId.of("UTC");
        }
    }
}


