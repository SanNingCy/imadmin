package com.seekweb4.chat.agora.roomduration.controller;

import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingConfigV2QueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingDetailQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingPageResp;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.roomduration.service.IMeetingStatsService;
import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于 RoomListV2Entity 的会议统计后台管理控制器
 * 新增版本，不影响原有功能
 * 
 * @author Admin Team
 * @version 2.0
 * @since 2.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/roomAdmin/meeting/stats/v2", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomDurationStatsAdminController {

    @Resource(name = "roomDurationStatsService")
    private IMeetingStatsService meetingStatsService;

    @Resource
    private RoomListV2Repository roomListV2Repository;

    /**
     * 分页查询会议列表
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/page</p>
     * 
     * @param req 查询请求
     * @return 分页响应
     */
    @PostMapping("/page")
    @ResponseBody
    public AjaxJson page(MeetingQueryReq req) {
        try {
            log.info("分页查询会议列表（RoomListV2Entity），查询条件：{}", req);
            MeetingPageResp result = meetingStatsService.pageQuery(req);
//            Map<String, Object> data = new HashMap<>();
//            data.put("data", result);
            return AjaxJson.success().put("page", result);
        } catch (Exception e) {
            log.error("分页查询会议列表失败", e);
            return AjaxJson.error("查询会议列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询会议室的详情（POST方式，支持JSON参数）
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/durationRoomMeeting</p>
     * 
     * @param req 查询请求
     * @return 会议室详情
     */
    @PostMapping("/durationRoomMeeting")
    @ResponseBody
    public AjaxJson getMeetingDurationPost(@RequestBody MeetingDetailQueryReq req) {
        try {
            log.info("查询会议室详情（RoomListV2Entity），roomId：{}", req.getRoomId());
            Map<String, Object> result = meetingStatsService.getMeetingDuration(req);
            Map<String, Object> data = new HashMap<>();
            data.put("data",result);
            return AjaxJson.success().put("data", data);
        } catch (Exception e) {
            log.error("查询会议室详情失败，roomId：{}", req.getRoomId(), e);
            return AjaxJson.error("查询会议室详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取平台总会议时长统计
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/duration</p>
     * 
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd）
     * @param dimension 统计维度（day=日，month=月，默认day）
     * @return 会议时长统计数据
     */
    @GetMapping("/duration")
    public AjaxJson getMeetingDurationStats(@RequestParam(required = false) String startTime,
                                           @RequestParam(required = false) String endTime,
                                           @RequestParam(defaultValue = "day") String dimension) {
        try {
            log.info("获取平台总会议时长统计（RoomListV2Entity），startTime：{}，endTime：{}，dimension：{}", startTime, endTime, dimension);
            Map<String, Object> result = meetingStatsService.getMeetingDurationStats(startTime, endTime, dimension);
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("获取平台总会议时长统计失败", e);
            return AjaxJson.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取平台总会议时长统计（POST方式，支持JSON参数）
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/duration</p>
     * 
     * @param body 请求参数（可选）
     * @return 会议时长统计数据
     */
    @PostMapping("/duration")
    public AjaxJson getMeetingDurationStatsPost(@RequestBody(required = false) Map<String, Object> body) {
        try {
            String startTime = null;
            String endTime = null;
            String dimension = "day";
            
            if (body != null) {
                startTime = body.get("startTime") == null ? null : String.valueOf(body.get("startTime"));
                endTime = body.get("endTime") == null ? null : String.valueOf(body.get("endTime"));
                dimension = body.get("dimension") == null ? "day" : String.valueOf(body.get("dimension"));
            }
            
            log.info("获取平台总会议时长统计（RoomListV2Entity），startTime：{}，endTime：{}，dimension：{}", startTime, endTime, dimension);
            Map<String, Object> result = meetingStatsService.getMeetingDurationStats(startTime, endTime, dimension);
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("获取平台总会议时长统计失败", e);
            return AjaxJson.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取正在进行中的会议数量
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/active/count</p>
     * 
     * @return 正在进行中的会议数量
     */
    @GetMapping("/active/count")
    public AjaxJson getActiveMeetingCount() {
        try {
            // 正在进行中的会议状态：active 和 inactive
            List<String> activeStatuses = Arrays.asList("active", "inactive");
            long activeCount = roomListV2Repository.findByStatusIn(activeStatuses).size();
            
            Map<String, Object> result = new HashMap<>();
            result.put("activeMeetingCount", activeCount);
            result.put("queryTime", System.currentTimeMillis());
            result.put("statuses", activeStatuses);
            result.put("description", "正在进行中的会议数量（包括active和inactive状态）- RoomListV2Entity版本");
            
            log.info("查询正在进行中的会议数量（RoomListV2Entity）：{}", activeCount);
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("查询正在进行中的会议数量失败", e);
            return AjaxJson.error("查询会议数量失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定群组正在进行中的会议数量
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/active/count/{groupId}</p>
     * 
     * @param groupId 群组ID
     * @return 指定群组正在进行中的会议数量
     */
    @GetMapping("/active/count/{groupId}")
    public AjaxJson getActiveMeetingCountByGroup(@PathVariable String groupId) {
        try {
            if (groupId == null || groupId.trim().isEmpty()) {
                return AjaxJson.error("群组ID不能为空");
            }
            
            // 正在进行中的会议状态：active 和 inactive
            List<String> activeStatuses = Arrays.asList("active", "inactive");
            long activeCount = roomListV2Repository.findByGroupIdAndStatusIn(groupId, activeStatuses).size();
            
            Map<String, Object> result = new HashMap<>();
            result.put("groupId", groupId);
            result.put("activeMeetingCount", activeCount);
            result.put("queryTime", System.currentTimeMillis());
            result.put("statuses", activeStatuses);
            result.put("description", "指定群组正在进行中的会议数量（包括active和inactive状态）- RoomListV2Entity版本");
            
            log.info("查询群组 {} 正在进行中的会议数量（RoomListV2Entity）：{}", groupId, activeCount);
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("查询群组 {} 正在进行中的会议数量失败", groupId, e);
            return AjaxJson.error("查询群组会议数量失败：" + e.getMessage());
        }
    }

    /**
     * 获取会议统计信息
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/statistics</p>
     * 
     * @param req 查询请求
     * @return 统计信息
     */
    @PostMapping("/statistics")
    public AjaxJson getMeetingStatistics(@RequestBody MeetingQueryReq req) {
        try {
            log.info("获取会议统计信息（RoomListV2Entity），查询条件：{}", req);
            Map<String, Object> result = meetingStatsService.getMeetingStatistics(req);
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("获取会议统计信息失败", e);
            return AjaxJson.error("获取统计信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户查询会议统计
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/user/{ownerId}</p>
     * 
     * @param ownerId 用户ID
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd）
     * @return 用户会议统计
     */
    @GetMapping("/user/{ownerId}")
    public AjaxJson getUserMeetingStats(@PathVariable String ownerId,
                                       @RequestParam(required = false) String startTime,
                                       @RequestParam(required = false) String endTime) {
        try {
            log.info("查询用户 {} 会议统计（RoomListV2Entity），时间范围：{} - {}", ownerId, startTime, endTime);
            Map<String, Object> result = meetingStatsService.getUserMeetingStats(ownerId, startTime, endTime);
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("查询用户会议统计失败，ownerId：{}", ownerId, e);
            return AjaxJson.error("查询用户会议统计失败：" + e.getMessage());
        }
    }

    /**
     * 根据会议状态查询统计
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/status/{status}</p>
     * 
     * @param status 会议状态
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd）
     * @return 状态统计
     */
    @GetMapping("/status/{status}")
    public AjaxJson getStatusMeetingStats(@PathVariable String status,
                                         @RequestParam(required = false) String startTime,
                                         @RequestParam(required = false) String endTime) {
        try {
            log.info("查询状态 {} 会议统计（RoomListV2Entity），时间范围：{} - {}", status, startTime, endTime);
            Map<String, Object> result = meetingStatsService.getStatusMeetingStats(status, startTime, endTime);
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("查询状态会议统计失败，status：{}", status, e);
            return AjaxJson.error("查询状态会议统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取会议进行时长统计
     * 
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/stats/v2/duration/ongoing</p>
     * 
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd）
     * @return 进行中会议时长统计
     */
    @GetMapping("/duration/ongoing")
    public AjaxJson getOngoingMeetingDurationStats(@RequestParam(required = false) String startTime,
                                                   @RequestParam(required = false) String endTime) {
        try {
            log.info("获取进行中会议时长统计（RoomListV2Entity），时间范围：{} - {}", startTime, endTime);
            
            // 查询进行中的会议（active + inactive状态）
            List<String> activeStatuses = Arrays.asList("active", "inactive");
            long activeCount = roomListV2Repository.findByStatusIn(activeStatuses).size();
            
            Map<String, Object> result = new HashMap<>();
            result.put("activeMeetingCount", activeCount);
            result.put("startTime", startTime);
            result.put("endTime", endTime);
            result.put("queryTime", System.currentTimeMillis());
            result.put("description", "进行中会议时长统计（包括active和inactive状态）- RoomListV2Entity版本");
            
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("获取进行中会议时长统计失败", e);
            return AjaxJson.error("获取进行中会议时长统计失败：" + e.getMessage());
        }
    }
}
