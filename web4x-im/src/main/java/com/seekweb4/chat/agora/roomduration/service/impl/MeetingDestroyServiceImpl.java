package com.seekweb4.chat.agora.roomduration.service.impl;

import com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq;
import com.seekweb4.chat.agora.roomduration.entity.MeetingEntity;
import com.seekweb4.chat.agora.roomduration.repository.MeetingRepository;
import com.seekweb4.chat.agora.roomduration.service.IMeetingDestroyService;
import com.seekweb4.chat.agora.service.IRoomV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 会议室销毁服务实现类
 * 提供移动端会议室销毁功能
 */
@Slf4j
@Service
public class MeetingDestroyServiceImpl implements IMeetingDestroyService {

    @Autowired
    private IRoomV2Service roomV2Service;

    @Autowired
    private MeetingRepository meetingRepository;

    @Value("${whitelist.token.appId:}")
    private String defaultAppId;

    @Override
    public Map<String, Object> destroyRoom(RoomDestroyReq request) throws Exception {
        log.info("开始销毁会议室，appId={}, sceneId={}, roomId={}", 
                request.getAppId(), request.getSceneId(), request.getRoomId());

        String roomId = request.getRoomId();
        
        // 参数校验
        if (!StringUtils.hasText(roomId)) {
            throw new IllegalArgumentException("roomId 不能为空");
        }

        // 1) 先查询本地 MeetingEntity 获取房间信息
        Optional<MeetingEntity> meetingOpt = meetingRepository.findByRoomId(roomId);
        if (!meetingOpt.isPresent()) {
            throw new RuntimeException("会议室不存在：" + roomId);
        }

        MeetingEntity entity = meetingOpt.get();
        
        // 检查会议室状态
        if ("destroyed".equals(entity.getStatus())) {
            throw new RuntimeException("会议室已经销毁：" + roomId);
        }

        // 2) 使用传入的 appId/sceneId 或默认值调用声网销毁
        String appId = StringUtils.hasText(request.getAppId()) ? request.getAppId() : defaultAppId;
        String sceneId = StringUtils.hasText(request.getSceneId()) ? request.getSceneId() : "VoiceRoomUIKit";
        
        try {
            // 重新构建销毁请求，确保使用正确的参数
            RoomDestroyReq destroyReq = new RoomDestroyReq();
            destroyReq.setAppId(appId);
            destroyReq.setSceneId(sceneId);
            destroyReq.setRoomId(roomId);
            
            roomV2Service.destroy(destroyReq);
            log.info("声网API销毁会议室成功，roomId={}", roomId);
        } catch (Exception e) {
            log.warn("声网API销毁会议室失败，roomId={}，将继续本地状态更新", roomId, e);
        }

        // 3) 更新本地 MeetingEntity 状态
        try {
            entity.setStatus("destroyed");
            entity.setUpdateTime(System.currentTimeMillis());
            if (entity.getEndTime() == null) {
                entity.setEndTime(System.currentTimeMillis());
            }
            meetingRepository.save(entity);
            log.info("本地MeetingEntity状态更新成功，roomId={}", roomId);
        } catch (Exception e) {
            log.warn("更新本地MeetingEntity状态失败，roomId={}", roomId, e);
        }

        // 4) 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("roomId", roomId);
        result.put("appId", appId);
        result.put("sceneId", sceneId);
        result.put("destroyTime", System.currentTimeMillis());
        result.put("success", true);

        log.info("销毁会议室完成，roomId={}", roomId);
        return result;
    }
}
