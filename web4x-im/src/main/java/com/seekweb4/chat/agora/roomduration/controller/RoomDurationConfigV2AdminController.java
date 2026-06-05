package com.seekweb4.chat.agora.roomduration.controller;

import com.seekweb4.chat.agora.roomadmin.bean.RoomDestroyReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.*;
import com.seekweb4.chat.agora.roomduration.service.IMeetingConfigV2AdminService;
import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Map;

/**
 * 基于 RoomListV2Entity 的会议配置后台管理控制器
 * 新增版本，不影响原有功能
 * 
 * @author Admin Team
 * @version 2.0
 * @since 2.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/roomAdmin/meeting/config/v2/room", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomDurationConfigV2AdminController {

    @Resource(name = "roomDurationConfigV2AdminService")
    private IMeetingConfigV2AdminService adminService;

    @PostMapping("/page")
    @ResponseBody
    public AjaxJson page(@RequestBody MeetingConfigV2QueryReq req) {
        return AjaxJson.success().put("data", adminService.pageQuery(req));
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public AjaxJson detail(@PathVariable("id") String id) {
        MeetingConfigV2Dto dto = adminService.getById(id);
        return dto == null ? AjaxJson.error("不存在") : AjaxJson.success().put("data", dto);
    }

    @PostMapping("/create")
    @ResponseBody
    public AjaxJson create(@Validated @RequestBody MeetingConfigV2CreateReq req) {
        String id = adminService.create(req);
        return AjaxJson.success().put("id", id);
    }

    @PostMapping("/update")
    @ResponseBody
    public AjaxJson update(@Validated @RequestBody MeetingConfigV2UpdateReq req) {
        boolean ok = adminService.update(req);
        return ok ? AjaxJson.success() : AjaxJson.error("更新失败");
    }

    @PostMapping("/insert")
    @ResponseBody
    public AjaxJson insert(@Validated @RequestBody MeetingConfigV2UpdateReq req) {
        boolean ok = adminService.insert(req);
        return ok ? AjaxJson.success() : AjaxJson.error("新增失败");
    }

    @PostMapping("/reqBackspace")
    @ResponseBody
    public AjaxJson reqBackspace(@Validated @RequestBody MeetingConfigV2UpdateReq req) {
        boolean ok = adminService.reqBackspace(req);
        return ok ? AjaxJson.success() : AjaxJson.error("删除");
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public AjaxJson delete(@PathVariable("id") String id) {
        boolean ok = adminService.deleteById(id);
        return ok ? AjaxJson.success() : AjaxJson.error("删除失败");
    }
    
    private String formatMillis(long millis) {
        return java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(java.time.ZoneId.systemDefault())
                .format(java.time.Instant.ofEpochMilli(millis));
    }

    private void putTimeStr(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Number) {
            map.put(key, formatMillis(((Number) v).longValue()));
        }
    }
    
    /**
     * 解散会议室（基于 RoomListV2Entity）
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meeting/config/v2/room/destroy</p>
     * 
     * @param request 解散请求
     * @return 解散结果
     */
    @PostMapping("/destroy")
    public AjaxJson destroyRoom(@Valid @RequestBody RoomDestroyReq request) {
        try {
            log.info("解散会议（RoomListV2Entity版本），roomId：{}，操作者：{}", request.getRoomId(), request.getOperatorId());
            Map<String, Object> result = adminService.destroyRoom(request);
            putTimeStr(result, "destroyTime");
            return AjaxJson.success("会议解散成功（RoomListV2Entity版本）").put("data", result);
        } catch (Exception e) {
            log.error("解散会议失败，roomId：{}", request.getRoomId(), e);
            return AjaxJson.error("解散会议失败：" + e.getMessage());
        }
    }
}
