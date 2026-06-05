package com.seekweb4.chat.agora.delayed;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.roomadmin.bean.RoomDestroyReq;
import com.seekweb4.chat.agora.roomadmin.service.RoomAdminService;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingCreateReq;
import com.seekweb4.chat.agora.service.IRoomV2Service;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 会议延时开启监听器
 * 处理会议延时开启的延时消息
 *
 * 功能：
 * 1. 查询当前是否存在这个groupId和ownerId的待开启会议
 * 2. 如果有则设置会议状态status为active状态，预设当前人数为1
 * 3. 如果没有说明会议记录已被取消，则无需处理
 */
@Slf4j
@Service
public class MeetingDelayedService {

    @Autowired
    private RoomListV2Repository roomListV2Repository;

    @Autowired
    private RoomAdminService roomAdminService;

    @Autowired
    private StringRedisUtils redisUtils;



    /** 应用ID，从配置文件读取 */
    @Value("${whitelist.token.appId}")
    private String appId;

    /**
     * 房间服务接口，处理房间相关的业务逻辑
     */
    @Resource
    private IRoomV2Service roomService;

    private final Gson gson = new Gson();

    public void handleMeetingOpen(DelayedMessageData messageData) {
        try {
            log.info("开始处理会议延时开启消息: {}", messageData);

            // 解析消息数据
            MeetingCreateReq meetingReq = parseMeetingRequest(messageData.getBusinessData());
            if (meetingReq == null) {
                log.warn("解析会议请求数据失败: {}", messageData);
                return;
            }

            String groupId = meetingReq.getGroupId();
            String ownerId = meetingReq.getOwnerId();

            if (groupId == null || ownerId == null) {
                log.warn("会议请求数据缺少必要字段: groupId={}, ownerId={}", groupId, ownerId);
                return;
            }

            log.info("处理会议延时开启: groupId={}, ownerId={}", groupId, ownerId);

            // 查询当前是否存在这个groupId和ownerId的待开启会议
            List<RoomListV2Entity> pendingMeetings = findPendingMeetings(groupId, ownerId);

            if (pendingMeetings.isEmpty()) {
                log.info("未找到待开启的会议，会议可能已被取消: groupId={}, ownerId={}", groupId, ownerId);
                return;
            }

            // 处理找到的待开启会议
            for (RoomListV2Entity meeting : pendingMeetings) {
                processPendingMeeting(meeting);
            }

            log.info("会议延时开启处理完成: groupId={}, ownerId={}, 处理会议数量={}",
                    groupId, ownerId, pendingMeetings.size());

        } catch (Exception e) {
            log.error("处理会议延时开启消息失败: {}", messageData, e);
        }
    }

    /**
     * 解析会议请求数据
     */
    private MeetingCreateReq parseMeetingRequest(String messageData) {
        try {
            return gson.fromJson(messageData, MeetingCreateReq.class);
        } catch (JsonSyntaxException e) {
            log.error("解析会议请求数据失败: {}", messageData, e);
            return null;
        }
    }

    /**
     * 查询待开启的会议
     * 查找状态为 pending_create 且属于指定群组和创建者的会议
     */
    private List<RoomListV2Entity> findPendingMeetings(String groupId, String ownerId) {
        try {
            // 查询群组内状态为 pending_create 的会议
            List<RoomListV2Entity> groupPendingMeetings = roomListV2Repository.findByGroupIdAndStatus(groupId, "pending_create");

            // 过滤出属于指定创建者的会议
            return groupPendingMeetings.stream()
                    .filter(meeting -> ownerId.equals(meeting.getOwnerId()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("查询待开启会议失败: groupId={}, ownerId={}", groupId, ownerId, e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 查询待开启的会议
     * 查找状态为 pending_create 且属于指定群组和创建者的会议
     */
    private List<RoomListV2Entity> findActiveMeetings(String groupId, String ownerId) {
        try {
            // 查询群组内状态为 pending_create 的会议
            List<RoomListV2Entity> groupPendingMeetings = roomListV2Repository.findByGroupIdAndStatusNot(groupId, "destroyed");

            // 过滤出属于指定创建者的会议
            return groupPendingMeetings.stream()
                    .filter(meeting -> ownerId.equals(meeting.getOwnerId()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("查询待关闭会议失败: groupId={}, ownerId={}", groupId, ownerId, e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 处理待开启的会议
     * 设置会议状态为 active，预设当前人数为1
     */
    private void processCloseMeeting(RoomListV2Entity meeting) {
        try {
            log.info("系统自动解散会议开始，roomId：{}，操作者：{}", meeting.getRoomId(), "system");
            RoomDestroyReq roomDestroyReq = new RoomDestroyReq();
            roomDestroyReq.setRoomId(meeting.getRoomId());
            roomDestroyReq.setOperatorId("System");
            //销毁会议室
            roomAdminService.destroyRoom(roomDestroyReq);
            com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq room = new com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq();
            room.setRoomId(meeting.getRoomId());
            room.setAppId(appId);
            //销毁会议室内的聊天室
            roomService.destroy(room);
            log.info("系统自动解散会议成功，roomId：{}，操作者：{}", meeting.getRoomId(), "system");
        } catch (Exception e) {
            log.error("处理待开启会议失败: meetingId={}, roomId={}",
                    meeting.getId(), meeting.getRoomId(), e);
        }
    }

    /**
     * 处理待开启的会议
     * 设置会议状态为 active，预设当前人数为1
     */
    private void processPendingMeeting(RoomListV2Entity meeting) {
        try {
            log.info("开始处理待开启会议: meetingId={}, roomId={}, groupId={}, ownerId={}",
                    meeting.getId(), meeting.getRoomId(), meeting.getGroupId(), meeting.getOwnerId());

            // 检查会议是否仍然有效（未过期）
            long currentTime = System.currentTimeMillis();
            if (meeting.getEndTime() != null && currentTime > meeting.getEndTime()) {
                log.info("会议已过期，无需开启: meetingId={}, endTime={}, currentTime={}",
                        meeting.getId(), meeting.getEndTime(), currentTime);
                return;
            }

            // 更新会议状态为 active
            meeting.setStatus("active");
            meeting.setCurrentUsers(1); // 预设当前人数为1
            meeting.setUpdateTime(currentTime);

            // 保存更新
            roomListV2Repository.save(meeting);

            log.info("会议状态已更新为active: meetingId={}, roomId={}, groupId={}, ownerId={}, currentUsers={}",
                    meeting.getId(), meeting.getRoomId(), meeting.getGroupId(),
                    meeting.getOwnerId(), meeting.getCurrentUsers());

        } catch (Exception e) {
            log.error("处理待开启会议失败: meetingId={}, roomId={}",
                    meeting.getId(), meeting.getRoomId(), e);
        }
    }

    public void handleMeetingClose(DelayedMessageData messageData) {
        // 解析消息数据
        MeetingCreateReq meetingReq = parseMeetingRequest(messageData.getBusinessData());
        if (meetingReq == null) {
            log.warn("解析会议请求数据失败: {}", messageData);
            return;
        }
        String ownerId = meetingReq.getOwnerId();
        String groupId = meetingReq.getGroupId();

        // 查询当前是否存在这个groupId和ownerId的可销毁会议
        List<RoomListV2Entity> pendingMeetings = findActiveMeetings(groupId, ownerId);

        if (pendingMeetings.isEmpty()) {
            log.info("未找到待关闭的会议，会议可能已被取消: groupId={}, ownerId={}", groupId, ownerId);
            return;
        }

        // 处理找到的可销毁会议
        for (RoomListV2Entity meeting : pendingMeetings) {
            processCloseMeeting(meeting);
        }
    }
}
