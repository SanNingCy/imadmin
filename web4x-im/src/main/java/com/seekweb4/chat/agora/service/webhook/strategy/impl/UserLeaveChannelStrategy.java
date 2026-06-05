package com.seekweb4.chat.agora.service.webhook.strategy.impl;

import com.google.gson.Gson;
import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import com.seekweb4.chat.agora.bean.enums.ChannelEventTypeEnum;
import com.seekweb4.chat.agora.bean.req.v2.EventCallBackReq;
import com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.roomduration.entity.MeetingEntity;
import com.seekweb4.chat.agora.roomduration.repository.MeetingRepository;
import com.seekweb4.chat.agora.service.IRoomV2Service;
import com.seekweb4.chat.agora.service.webhook.strategy.WebhookEventProcessStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Optional;


/**
 * 主播退出离开频道事件处理策略
 * 
 * <p>处理用户离开RTC频道的webhook事件，包括：</p>
 * <ul>
 *   <li>通过用户管理服务记录用户离开</li>
 *   <li>通过频道管理服务更新频道用户统计</li>
 *   <li>确保数据一致性和业务逻辑统一</li>
 * </ul>
 * 
 * @author Agora
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserLeaveChannelStrategy implements WebhookEventProcessStrategy {
    
    private final IRoomV2Service iRoomV2Service;

    @Resource
    @Lazy()
    private RoomListV2Repository roomListV2Repository;
    
    @Override
    public Boolean process(EventCallBackReq event) {
        log.info("开始处理观众离开频道事件: noticeId={}, appId={}", event.getNoticeId(), event.getProductId());
        try {
            // 提取事件数据
            Map<String,Object> payload = event.getPayload();
            //String ownerId = (String) payload.get("account"); // 等价 ownerId
            String channelName = (String)payload.get("channelName");
            //1.查询房间 ownerId + channelName
            Optional<RoomListV2Entity> byRoomId = roomListV2Repository.findByRoomId(channelName);
            if (!byRoomId.isPresent()) {
                log.info("未找到会议");
                return Boolean.FALSE;
            }
            RoomListV2Entity entity = byRoomId.get();
            log.info("观众加入频道，用户数据:{}",new Gson().toJson(entity));
            log.info("观众加入频道，getCurrentUsers数据:{}",entity.getCurrentUsers());
            entity.setCurrentUsers(entity.getCurrentUsers()-1);
            //更新会议人数
            roomListV2Repository.save(entity);
            log.info("观众离开频道事件处理成功: channelName={}", channelName);
            return true;
        } catch (Exception e) {
            log.error("观众离开频道事件处理失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public ChannelEventTypeEnum getSupportedEventType() {
        return ChannelEventTypeEnum.USER_LEAVE_CHANNEL;
    }
}