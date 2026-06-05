package com.seekweb4.chat.agora.roomduration.service.impl;

import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.roomduration.entity.MeetingConfigV2Entity;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingCreateReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingGroupQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingSettingsUpdateReq;
import com.seekweb4.chat.agora.roomduration.service.IMeetingService;
import com.seekweb4.chat.agora.roomduration.service.IMeetingConfigV2Service;
import com.seekweb4.chat.agora.roomduration.service.IUserBalanceService;
import com.seekweb4.chat.agora.service.IRoomV2Service;
import com.seekweb4.chat.modules.group.service.GroupService;
import com.seekweb4.chat.modules.group.web.GroupController;
import com.seekweb4.chat.modules.group.service.GroupService;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.member.service.MemberService;
import com.seekweb4.chat.modules.member.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

/**
 * 基于 RoomListV2Entity 的会议室服务实现
 * 替代原有的 MeetingEntity 功能
 */
@Slf4j
@Service("roomDurationMeetingService")
public class RoomDurationServiceImpl implements IMeetingService {

    @Resource
    private RoomListV2Repository roomListV2Repository;

    @Resource
    private IUserBalanceService userBalanceService;

    @Resource
    private IMeetingConfigV2Service meetingConfigV2Service;

    @Resource
    private IRoomV2Service roomV2Service;

    @Value("${whitelist.token.appId:}")
    private String defaultAppId;

    @Resource
    private GroupService groupService;

    @Resource
    private MemberService memberService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DEFAULT_TIME_ZONE = "UTC+8";
    private static final boolean DEFAULT_ALL_MIC = true;
    private static final boolean DEFAULT_ALL_MUTE = false;
    private static final String DEFAULT_SCENE_ID = "live_streaming";

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

//        req.setStartTime();

        // 开始时间校验：不可早于当前时间；不可超过30天后
//        long maxAhead = 30L * 24 * 60 * 60 * 1000; // 30天
//        if (startEpochMillis < now) {
//            throw new IllegalArgumentException("开始时间不能早于当前时间");
//        }
//        if (startEpochMillis - now > maxAhead) {
//            throw new IllegalArgumentException("开始时间不可超过30天");
//        }

        // 1) 检查用户是否有未销毁的会议（不基于时间，只基于状态）
        if (req.getOwnerId() != null) {
            long activeMeetings = roomListV2Repository.findByOwnerIdAndStatusNot(req.getOwnerId(), "destroyed").size();
            if (activeMeetings > 0) {
                throw new IllegalArgumentException("您已有会议正在进行中，暂时无法创建新会议，请处理当前会议");
            }
        }

        // 2) 余额校验并扣费
        if (req.getOwnerId() != null && req.getMeetingTime() != null && req.getMeetingMaxUsers() != null) {
            try {
                Map<String, BigDecimal> calc = calculateTokens(req.getOwnerId(), req.getMeetingTime(), req.getMeetingMaxUsers());
                BigDecimal available = calc.get("availableTokens");
                BigDecimal need = calc.get("meetingTokens");
                if (available == null || need == null) {
                    throw new IllegalArgumentException("参数异常: 计算结果缺失");
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
                    log.warn("更新withhold失败 userId={}, amount={}", req.getOwnerId(), deductedAmount, e);
                }
            } catch (IllegalArgumentException ex) {
                throw ex;
            } catch (Exception e) {
                throw new IllegalArgumentException("参数异常: " + e.getMessage());
            }
        }

        // 生成会议室ID
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
            }
        }

        // 获取配置信息
        String mtName = resolveMeetingTimeLabel(Integer.valueOf(req.getMeetingTime()));
        Integer minMeetingTime = getMinMeetingTimeFromConfig();
        String minMeetingTimeName = getMinMeetingTimeNameFromConfig();

        RoomListV2Entity entity = new RoomListV2Entity()
                .setRoomId(roomId)
                .setIdNo(userIdNo)
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
                .setUpdateTime(now)
                .setAppId(defaultAppId)
                .setSceneId(DEFAULT_SCENE_ID);

        // 4. 补充群名称与用户昵称（只查询不改动数据库）
        try {
            if (req.getGroupId() != null) {
                Group group = groupService.get(req.getGroupId());
                if (group != null && group.getName() != null) {
                    entity.setGroupName(group.getName());
                }else {
                    entity.setGroupName("");
                }
            }
        } catch (Exception e) {
            log.warn("查询群名称失败 groupId={}", req.getGroupId(), e);
        }
        try {
            if (req.getOwnerId() != null) {
                Member member = memberService.get(req.getOwnerId());
                if (member != null && member.getNickname() != null) {
                    entity.setOwnerNickname(member.getNickname());
                }else {
                    entity.setOwnerNickname("");
                }
            }
        } catch (Exception e) {
            log.warn("查询用户昵称失败 ownerId={}", req.getOwnerId(), e);
        }

        roomListV2Repository.save(entity);
        log.info("Room saved. groupId={}, roomName={}, ownerId={}", req.getGroupId(), req.getRoomName(), req.getOwnerId());

        // 返回创建的会议数据
        return buildRoomResponse(entity);
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

        // 校验 meetingMaxUsers 是否在配置的 userTierOptions 中
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

        // 余额与折扣价
        BigDecimal available = userBalanceService.getUserBalance(ownerId).setScale(1, java.math.RoundingMode.HALF_UP);
        BigDecimal discountMeetingTokens = meetingTokens;

        Map<String, java.math.BigDecimal> result = new java.util.HashMap<>();
        result.put("availableTokens", available);
        result.put("meetingTokens", meetingTokens);
        result.put("discountMeetingTokens", discountMeetingTokens);
        return result;
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
        List<RoomListV2Entity> rooms = roomListV2Repository.findByGroupIdAndStatusNot(groupId, "destroyed");
        RoomListV2Entity room = null;
        for (RoomListV2Entity r : rooms) {
            // 会议未结束的条件：endTime为null或者endTime大于当前时间
            boolean notEnded = (r.getEndTime() == null || r.getEndTime() > currentTime);
            if (notEnded) {
                if (room == null || (r.getStartTime() != null && room.getStartTime() != null && r.getStartTime() > room.getStartTime())) {
                    room = r;
                }
            }
        }
        
        if (room == null) {
            return data;
        }
        
        return buildRoomResponse(room);
    }

    @Override
    public boolean addGroupTime(String groupId, String ownerId, String meetingTime, String roomId) throws Exception {
        if (ownerId == null || meetingTime == null) {
            throw new IllegalArgumentException("参数异常：ownerId和meetingTime为必传参数");
        }
        
        RoomListV2Entity room;
        if (roomId != null && !roomId.trim().isEmpty()) {
            Optional<RoomListV2Entity> roomOpt = roomListV2Repository.findByRoomIdAndStatusNot(roomId,"destroyed");
            if (!roomOpt.isPresent()) {
                throw new IllegalArgumentException("会议不存在：" + roomId);
            }
            room = roomOpt.get();
            if (groupId != null && !groupId.trim().isEmpty() && !groupId.equals(room.getGroupId())) {
                throw new IllegalArgumentException("会议不属于指定群组");
            }
        } else {
            if (groupId == null || groupId.trim().isEmpty()) {
                throw new IllegalArgumentException("参数异常：当roomId为空时，groupId为必传参数");
            }
            List<RoomListV2Entity> rooms = roomListV2Repository.findByGroupId(groupId);
            if (rooms.isEmpty()) {
                return false;
            }
            room = rooms.get(0); // 取第一个
        }

        if (!Boolean.TRUE.equals(room.getIsNonPayPwd())) {
            throw new IllegalArgumentException("暂未开启免密支付不支持该操作！");
        }

        Map<String, BigDecimal> calc = calculateTokens(ownerId, meetingTime, String.valueOf(room.getMeetingMaxUsers()));
        BigDecimal need = calc.get("meetingTokens");
        BigDecimal available = calc.get("availableTokens");
        if (available.compareTo(need) < 0) {
            throw new IllegalArgumentException("创建失败: 您的Token不足");
        }

        String info = String.format("会议室：%s，加时：%s分钟，人数：%s人，消耗积分：%s", 
                room.getRoomName(), meetingTime, room.getMeetingMaxUsers(), need);
        Map<String, BigDecimal> deducted = deductTokens(ownerId, meetingTime, String.valueOf(room.getMeetingMaxUsers()), "会议加时扣费", info);
        BigDecimal deductAmount = deducted.get("deductTokens");
        userBalanceService.addUserWithhold(ownerId, deductAmount);

        int addMinutes = Integer.parseInt(meetingTime);
        long addMillis = addMinutes * 60_000L;
        Long base = room.getEndTime() != null ? room.getEndTime() : room.getStartTime();
        // 添加会议时长的时候，为meetingTime也增加了时长，用于自动销毁，不加这个的话，到原来的时间就销毁了状态，但是还没结束
        room.setMeetingTime(room.getMeetingTime() + addMinutes);
        room.setEndTime(base + addMillis);
        room.setUpdateTime(System.currentTimeMillis());
        roomListV2Repository.save(room);
        return true;
    }

    @Override
    public Map<String, Object> getGroupActiveMeetings(String groupId) throws Exception {
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new IllegalArgumentException("群ID不能为空");
        }
        
        long now = System.currentTimeMillis();
        List<RoomListV2Entity> activeRooms = roomListV2Repository.findByGroupIdAndStatusNot(groupId, "destroyed");
        
        List<Map<String, Object>> meetingList = new ArrayList<>();
        for (RoomListV2Entity room : activeRooms) {
            if (room.getEndTime() == null || now <= room.getEndTime()) {
                Map<String, Object> meetingInfo = buildRoomResponse(room);
                meetingList.add(meetingInfo);
            }
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
        
        List<RoomListV2Entity> activeRooms = new ArrayList<>();
        List<RoomListV2Entity> activeList = roomListV2Repository.findByGroupIdAndStatus(groupId, "active");
        List<RoomListV2Entity> inactiveList = roomListV2Repository.findByGroupIdAndStatus(groupId, "inactive");
        activeRooms.addAll(activeList);
        activeRooms.addAll(inactiveList);
        
        List<Map<String, Object>> meetingList = new ArrayList<>();
        for (RoomListV2Entity room : activeRooms) {
            Map<String, Object> meetingInfo = buildRoomResponse(room);
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
    public boolean updateMeetingSettings(MeetingSettingsUpdateReq req) throws Exception {
        log.info("updateMeetingSettings, roomId: {}, allMic: {}, allMute: {}, status: {}", 
                req.getRoomId(), req.getAllMic(), req.getAllMute(), req.getStatus());
        
        if (req.getRoomId() == null || req.getRoomId().trim().isEmpty()) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }
        
        Optional<RoomListV2Entity> roomOpt = roomListV2Repository.findByRoomId(req.getRoomId());
        if (!roomOpt.isPresent()) {
            if ("destroyed".equals(req.getStatus())) {
                log.info("销毁成功：roomId: {}", req.getRoomId());
                return true;
            }
            throw new RuntimeException("会议室不存在");
        }
        
        RoomListV2Entity room = roomOpt.get();
        
        if (req.getAllMic() != null) {
            room.setAllMic(req.getAllMic());
        }
        if (req.getAllMute() != null) {
            room.setAllMute(req.getAllMute());
        }
        
        if (req.getStatus() != null && !req.getStatus().trim().isEmpty()) {
            if (!isValidStatusTransition(room.getStatus(), req.getStatus())) {
                throw new IllegalArgumentException("状态转换不合法：从 " + room.getStatus() + " 到 " + req.getStatus());
            }
            room.setStatus(req.getStatus());
        }
        
        room.setUpdateTime(System.currentTimeMillis());
        roomListV2Repository.save(room);
        
        log.info("updateMeetingSettings, success, roomId: {}, status: {}", req.getRoomId(), room.getStatus());
        return true;
    }

    @Override
    public Map<String, Object> getMeetingByRoomId(String roomId) throws Exception {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("会议室ID不能为空");
        }
        
        Optional<RoomListV2Entity> roomOpt = roomListV2Repository.findByRoomId(roomId);
        if (!roomOpt.isPresent()) {
            throw new IllegalArgumentException("会议室不存在：" + roomId);
        }
        
        RoomListV2Entity room = roomOpt.get();
        return buildRoomResponse(room);
    }

    /**
     * 构建房间响应数据
     */
    private Map<String, Object> buildRoomResponse(RoomListV2Entity room) {
        Map<String, Object> data = new HashMap<>();

        if (room == null) {
            log.warn("buildRoomResponse: room参数为null");
            return data;
        }

        // 1. 自动映射所有基础字段
        Map<String, Object> baseFields = convertRoomEntityToMap(room);
        data.putAll(baseFields);
        
        // 2. 添加计算字段和特殊处理字段
        long now = System.currentTimeMillis();
        
        // 计算会议状态
        String meetingStatus;
        Long startTime = room.getStartTime();
        Long endTime = room.getEndTime();
        
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
        
        // 3. 覆盖/添加特殊字段
        data.put("meetingId", room.getId());
        data.put("currentSize", room.getCurrentUsers());
        data.put("meetingStatus", meetingStatus);
        data.put("remainingTime", remaining);
        data.put("isNonPayPwd", Boolean.TRUE.equals(room.getIsNonPayPwd()));
        data.put("startTime", room.getStartTime());
        return data;
    }

    /**
     * 使用反射将RoomListV2Entity转换为Map
     */
    private Map<String, Object> convertRoomEntityToMap(RoomListV2Entity entity) {
        Map<String, Object> result = new HashMap<>();

        if (entity == null) {
            log.warn("convertRoomEntityToMap: entity参数为null");
            return result;
        }

        try {
            Field[] fields = RoomListV2Entity.class.getDeclaredFields();
            
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                
                field.setAccessible(true);
                Object value = field.get(entity);
                String fieldName = field.getName();
                result.put(fieldName, value);
            }
        } catch (Exception e) {
            log.error("反射转换RoomListV2Entity失败", e);
        }
        
        return result;
    }

    /**
     * 验证状态转换是否合法
     */
    private boolean isValidStatusTransition(String oldStatus, String newStatus) {
        if (oldStatus == null || newStatus == null) {
            return false;
        }
        
        if (oldStatus.equals(newStatus)) {
            return true;
        }
        
        if ("destroyed".equals(oldStatus)) {
            return false;
        }
        
        if ("destroyed".equals(newStatus)) {
            return true;
        }
        
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

    /**
     * 解析 meetingTime 对应配置的 label
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
            return null;
        }
    }

    private Long parseStartTime(String startTime, String timeZone) {
        if (startTime == null || startTime.trim().isEmpty()) {
            throw new IllegalArgumentException("参数异常: startTime为空");
        }
        String s = startTime.trim();
        if (s.matches("^\\d+$")) {
            try {
                long ts = Long.parseLong(s);
                if (ts < 1_000_000_000_000L) {
                    ts = ts * 1000L;
                }
                return ts;
            } catch (Exception e) {
                throw new IllegalArgumentException("参数异常: startTime时间戳格式错误");
            }
        }
        try {
            ZoneId zoneId = parseZoneId(timeZone);
            LocalDateTime localDateTime = LocalDateTime.parse(s, DATE_TIME_FORMATTER);
            return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
        } catch (Exception e) {
            throw new IllegalArgumentException("参数异常: startTime日期格式错误");
        }
    }

    private ZoneId parseZoneId(String timeZone) {
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
