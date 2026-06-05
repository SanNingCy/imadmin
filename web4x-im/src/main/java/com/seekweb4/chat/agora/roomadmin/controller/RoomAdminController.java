package com.seekweb4.chat.agora.roomadmin.controller;

import com.seekweb4.chat.agora.roomadmin.bean.*;
import com.seekweb4.chat.agora.roomadmin.service.RoomAdminService;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.web.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 会议室后台管理系统控制器
 * 
 * <p>提供会议室的后台管理功能，包括查询、解散等操作。</p>
 * 
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>会议列表查询 - 支持分页、筛选、排序</li>
 *   <li>会议聊天室查询 - 查看会议聊天室详情</li>
 *   <li>解散会议 - 强制解散指定会议</li>
 * </ul>
 * 
 * <p><strong>API版本：</strong>v1</p>
 * <p><strong>路径前缀：</strong>/api/admin/room</p>
 * <p><strong>权限要求：</strong>需要管理员权限</p>
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/roomAdmin/meetingRoom")
public class RoomAdminController extends BaseController {

    @Autowired
    private RoomAdminService roomAdminService;

    /**
     * 获取会议列表
     * 
     * <p>分页查询会议列表，支持多种筛选条件和排序方式。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/api/admin/room/list</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>查询条件：</strong></p>
     * <ul>
     *   <li>房间ID - 精确匹配</li>
     *   <li>群ID - 精确匹配</li>
     *   <li>状态 - 支持多状态筛选</li>
     *   <li>创建时间范围 - 时间区间筛选</li>
     *   <li>房间名称 - 模糊匹配</li>
     *   <li>创建者ID - 精确匹配</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "pageNum": 1,
     *   "pageSize": 20,
     *   "roomId": "room_123456",
     *   "groupId": "group_12345",
     *   "status": ["active", "inactive"],
     *   "startTime": 1694073000000,
     *   "endTime": 1694159400000,
     *   "roomName": "会议室",
     *   "ownerId": "user_123",
     *   "orderBy": "createTime",
     *   "orderDirection": "desc"
     * }
     * </pre>
     * 
     * @param request 查询请求对象
     * @return 统一响应对象，包含分页的会议列表
     */
    @PostMapping("/list")
    public AjaxJson getRoomList(@Valid RoomListQueryReq request) {
        try {
            log.info("获取会议列表，查询条件：{}", request);
            RoomListResponse result = roomAdminService.getRoomList(request);
            Map<String, Object> data = toMap(result);
            Object converted = convertTimeFields(data);
            return AjaxJson.success().put("data", converted);
        } catch (Exception e) {
            log.error("获取会议列表失败", e);
            return AjaxJson.error("获取会议列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取会议聊天室详情
     * 
     * <p>根据会议ID获取聊天室的详细信息。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/api/admin/room/chatroom/detail</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "roomId": "room_123456"
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "roomId": "room_123456",
     *     "chatRoomId": "chat_room_123456",
     *     "roomName": "会议室001",
     *     "status": "active",
     *     "userCount": 15,
     *     "maxUsers": 1000,
     *     "ownerId": "user_123",
     *     "groupId": "group_12345",
     *     "createTime": 1694073000000,
     *     "lastActiveTime": 1694073500000,
     *     "chatRoomConfig": {
     *       "maxUsers": 1000,
     *       "name": "会议室001"
     *     }
     *   },
     *   "success": true
     * }
     * </pre>
     * 
     * @param request 查询请求对象
     * @return 统一响应对象，包含聊天室详细信息
     */
    @PostMapping("/chatroom/detail")
    public AjaxJson getChatRoomDetail(@Valid RoomDetailQueryReq request) {
        try {
            log.info("获取会议聊天室详情，roomId：{}", request.getRoomId());
            RoomDetailResponse result = roomAdminService.getChatRoomDetail(request);
            Map<String, Object> data = toMap(result);
            Object converted = convertTimeFields(data);
            return AjaxJson.success().put("data", converted);
        } catch (Exception e) {
            log.error("获取会议聊天室详情失败，roomId：{}", request.getRoomId(), e);
            return AjaxJson.error("获取会议聊天室详情失败：" + e.getMessage());
        }
    }

    /**
     * 解散会议
     * 
     * <p>强制解散指定的会议，释放所有相关资源。</p>
     * <p>解散操作是不可逆的，请谨慎使用。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/api/admin/room/destroy</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>操作影响：</strong></p>
     * <ul>
     *   <li>移除会议内所有用户</li>
     *   <li>清理会议相关数据</li>
     *   <li>释放系统资源</li>
     *   <li>停止会议相关服务</li>
     *   <li>更新会议状态为已销毁</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "roomId": "room_123456",
     *   "reason": "管理员强制解散",
     *   "operatorId": "admin_001"
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "会议解散成功",
     *   "data": {
     *     "roomId": "room_123456",
     *     "destroyTime": 1694073600000,
     *     "operatorId": "admin_001"
     *   },
     *   "success": true
     * }
     * </pre>
     * 
     * @param request 解散请求对象
     * @return 统一响应对象，包含解散结果
     */
    @PostMapping("/destroy")
    public AjaxJson destroyRoom(@Valid RoomDestroyReq request) {
        try {
            log.info("解散会议，roomId：{}，操作者：{}", request.getRoomId(), request.getOperatorId());
            Map<String, Object> result = roomAdminService.destroyRoom(request);
            putTimeStr(result, "destroyTime");
            return AjaxJson.success("会议解散成功").put("data", result);
        } catch (Exception e) {
            log.error("解散会议失败，roomId：{}", request.getRoomId(), e);
            return AjaxJson.error("解散会议失败：" + e.getMessage());
        }
    }

    /**
     * 批量解散会议
     * 
     * <p>批量解散多个会议，用于批量管理操作。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/api/admin/room/batchDestroy</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "roomIds": ["room_123456", "room_789012"],
     *   "reason": "批量清理过期会议",
     *   "operatorId": "admin_001"
     * }
     * </pre>
     * 
     * @param request 批量解散请求对象
     * @return 统一响应对象，包含批量解散结果
     */
    @PostMapping("/batchDestroy")
    public AjaxJson batchDestroyRooms(@Valid @RequestBody Map<String, Object> request) {
        try {
            log.info("批量解散会议，请求：{}", request);
            Map<String, Object> result = roomAdminService.batchDestroyRooms(request);
            // 标准化 results 内每条记录的时间展示（覆盖原字段）
            Object results = result.get("results");
            if (results instanceof java.util.List) {
                for (Object item : (java.util.List<?>) results) {
                    if (item instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> m = (Map<String, Object>) item;
                        putTimeStr(m, "destroyTime");
                    }
                }
            }
            return AjaxJson.success("批量解散完成").put("data", result);
        } catch (Exception e) {
            log.error("批量解散会议失败", e);
            return AjaxJson.error("批量解散会议失败：" + e.getMessage());
        }
    }

    /**
     * 获取会议室在线用户列表
     *
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meetingRoom/users</p>
     *
     * @param roomId 会议室ID
     * @return 在线用户列表
     */
    @GetMapping("/users")
    public AjaxJson getRoomUsers(@RequestParam String roomId) {
        try {
            log.info("获取会议室用户列表，roomId：{}", roomId);
            List<Map<String, Object>> result = roomAdminService.getRoomUsers(roomId);
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("获取会议室用户列表失败，roomId：{}", roomId, e);
            return AjaxJson.error("获取会议室用户列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取会议统计数据
     *
     * <p><strong>HTTP方法：</strong>GET</p>
     * <p><strong>请求路径：</strong>/roomAdmin/meetingRoom/stats</p>
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param groupId 群ID（可选）
     * @return 统计数据
     */
    @GetMapping("/stats")
    public AjaxJson getRoomStats(@RequestParam(required = false) String startTime,
                                 @RequestParam(required = false) String endTime,
                                 @RequestParam(required = false) String groupId) {
        try {
            Long startMs = parseToMillis(startTime);
            Long endMs = parseToMillis(endTime);
            log.info("获取会议室统计数据，startTime：{}，endTime：{}，groupId：{}", startMs, endMs, groupId);
            Map<String, Object> result = roomAdminService.getRoomStats(startMs, endMs, groupId);
            // 将 queryTime 转换为可读字符串
            Object qt = result.get("queryTime");
            if (qt instanceof Number) {
                long ms = ((Number) qt).longValue();
                result.put("queryTime", formatMillis(ms));
            }
            return AjaxJson.success().put("data", result);
        } catch (Exception e) {
            log.error("获取会议室统计数据失败", e);
            return AjaxJson.error("获取会议室统计数据失败：" + e.getMessage());
        }
    }

    private Long parseToMillis(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String v = value.trim();
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException ignore) {
        }
        // 支持 yyyy-MM-dd
        try {
            LocalDate d = LocalDate.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return d.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DateTimeParseException ignore) {
        }
        // 尝试 ISO-8601 日期时间
        try {
            LocalDateTime dt = LocalDateTime.parse(v);
            return dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("无法解析时间参数: " + value);
        }
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

    private Map<String, Object> toMap(Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(obj, new TypeReference<Map<String, Object>>(){});
    }

    private Object convertTimeFields(Object value) {
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> e : map.entrySet()) {
                String k = e.getKey();
                Object v = e.getValue();
                if (v instanceof Number && isTimeKey(k)) {
                    map.put(k, formatMillis(((Number) v).longValue()));
                } else {
                    map.put(k, convertTimeFields(v));
                }
            }
            return map;
        } else if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                list.set(i, convertTimeFields(list.get(i)));
            }
            return list;
        } else {
            return value;
        }
    }

    private boolean isTimeKey(String key) {
        return "createTime".equals(key) || "updateTime".equals(key) || "lastActiveTime".equals(key) || "destroyTime".equals(key) || "queryTime".equals(key);
    }
}
