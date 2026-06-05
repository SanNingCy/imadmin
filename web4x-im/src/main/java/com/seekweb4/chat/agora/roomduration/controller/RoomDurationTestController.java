package com.seekweb4.chat.agora.roomduration.controller;

import com.seekweb4.chat.agora.roomduration.service.IMeetingService;
import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 基于 RoomListV2Entity 的会议室测试控制器
 * 用于验证新实现的功能
 */
@Slf4j
@RestController
@RequestMapping(value = "/room-duration-test", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomDurationTestController {

    @Resource(name = "roomDurationMeetingService")
    private IMeetingService roomDurationService;

    /**
     * 测试创建会议室
     */
    @PostMapping("/create")
    public AjaxJson testCreateRoom(@RequestBody Map<String, Object> body) {
        try {
            log.info("测试创建会议室，参数：{}", body);
            // 这里可以添加测试逻辑
            return AjaxJson.success("基于 RoomListV2Entity 的服务已就绪");
        } catch (Exception e) {
            log.error("测试创建会议室失败", e);
            return AjaxJson.error("测试失败：" + e.getMessage());
        }
    }

    /**
     * 测试查询会议室
     */
    @GetMapping("/room/{roomId}")
    public AjaxJson testGetRoom(@PathVariable String roomId) {
        try {
            log.info("测试查询会议室，roomId：{}", roomId);
            Map<String, Object> data = roomDurationService.getMeetingByRoomId(roomId);
            return AjaxJson.success().setData(data);
        } catch (Exception e) {
            log.error("测试查询会议室失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }
}
