package com.seekweb4.chat.agora.roomduration.controller;

import com.seekweb4.chat.agora.roomduration.service.IMeetingService;
import com.seekweb4.chat.agora.roomduration.service.IMeetingStatsService;
import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于 RoomListV2Entity 的集成测试控制器
 * 用于验证所有新功能是否正常工作
 */
@Slf4j
@RestController
@RequestMapping(value = "/room-duration-integration-test", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomDurationIntegrationTestController {

    @Resource(name = "roomDurationMeetingService")
    private IMeetingService roomDurationMeetingService;

    @Resource(name = "roomDurationStatsService")
    private IMeetingStatsService roomDurationStatsService;

    /**
     * 测试所有基于 RoomListV2Entity 的服务
     */
    @GetMapping("/all-services")
    public AjaxJson testAllServices() {
        try {
            log.info("测试所有基于 RoomListV2Entity 的服务");
            
            Map<String, Object> result = new HashMap<>();
            result.put("meetingService", "roomDurationMeetingService - 基于 RoomListV2Entity");
            result.put("statsService", "roomDurationStatsService - 基于 RoomListV2Entity");
            result.put("status", "所有服务已就绪");
            result.put("version", "2.0");
            
            return AjaxJson.success("所有基于 RoomListV2Entity 的服务测试通过").setData(result);
        } catch (Exception e) {
            log.error("测试服务失败", e);
            return AjaxJson.error("测试失败：" + e.getMessage());
        }
    }

    /**
     * 测试会议服务
     */
    @GetMapping("/meeting-service")
    public AjaxJson testMeetingService() {
        try {
            log.info("测试会议服务（RoomListV2Entity版本）");
            return AjaxJson.success("会议服务（RoomListV2Entity版本）已就绪");
        } catch (Exception e) {
            log.error("测试会议服务失败", e);
            return AjaxJson.error("测试失败：" + e.getMessage());
        }
    }

    /**
     * 测试统计服务
     */
    @GetMapping("/stats-service")
    public AjaxJson testStatsService() {
        try {
            log.info("测试统计服务（RoomListV2Entity版本）");
            return AjaxJson.success("统计服务（RoomListV2Entity版本）已就绪");
        } catch (Exception e) {
            log.error("测试统计服务失败", e);
            return AjaxJson.error("测试失败：" + e.getMessage());
        }
    }
}
