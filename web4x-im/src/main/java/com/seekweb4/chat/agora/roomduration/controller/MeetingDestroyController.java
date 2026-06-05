package com.seekweb4.chat.agora.roomduration.controller;

import com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq;
import com.seekweb4.chat.agora.roomduration.service.IMeetingDestroyService;
import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 移动端：会议室销毁接口（参考声网 /v2/room/destroy）
 * 路由：POST /v2/room/destroy
 * 说明：移动端销毁会议室，同时解散声网房间并更新本地状态
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/meetings/v2/room", produces = MediaType.APPLICATION_JSON_VALUE)
public class MeetingDestroyController {

    @Autowired
    private IMeetingDestroyService meetingDestroyService;

    /**
     * 销毁会议室（移动端接口）
     * POST /meetings/v2/room/destroy
     * 
     * 请求体：RoomDestroyReq（appId, sceneId, roomId）
     */
    @PostMapping("/destroy")
    public AjaxJson destroy(@Valid @RequestBody RoomDestroyReq request) {
        try {
            log.info("移动端销毁会议室，appId={}, sceneId={}, roomId={}", 
                    request.getAppId(), request.getSceneId(), request.getRoomId());
            
            Map<String, Object> result = meetingDestroyService.destroyRoom(request);
            return AjaxJson.success("会议室销毁成功").put("data", result);
        } catch (Exception e) {
            log.error("移动端销毁会议室失败，roomId={}", request.getRoomId(), e);
            return AjaxJson.error("销毁会议室失败：" + e.getMessage());
        }
    }
}
