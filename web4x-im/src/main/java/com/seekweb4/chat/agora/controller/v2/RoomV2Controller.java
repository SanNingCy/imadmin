package com.seekweb4.chat.agora.controller.v2;

import com.seekweb4.chat.agora.bean.dto.R;
import com.seekweb4.chat.agora.bean.dto.v2.*;
import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import com.seekweb4.chat.agora.bean.req.v2.*;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.service.IRoomV2Service;
import com.seekweb4.chat.agora.utils.RedisUtil;
import com.seekweb4.chat.common.json.AjaxJson;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 房间管理控制器 V2版本
 * 
 * <p>该控制器提供房间相关的REST API接口，支持房间的全生命周期管理。</p>
 * <p>房间是应用中用户交互的基本单元，可用于多种业务场景。</p>
 * 
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>创建房间 - 支持多种房间类型和配置</li>
 *   <li>销毁房间 - 安全释放房间资源</li>
 *   <li>更新房间 - 动态修改房间属性</li>
 *   <li>查询房间 - 获取房间详细信息</li>
 *   <li>房间列表 - 分页获取房间列表</li>
 * </ul>
 * 
 * <p><strong>API版本：</strong>v2</p>
 * <p><strong>路径前缀：</strong>/v2/room</p>
 * <p><strong>响应格式：</strong>JSON</p>
 * 
 * <p><strong>应用场景：</strong></p>
 * <ul>
 *   <li>视频会议室管理</li>
 *   <li>在线教育课堂</li>
 *   <li>直播间管理</li>
 *   <li>游戏房间系统</li>
 *   <li>语音聊天室</li>
 * </ul>
 * 
 * @author UIKit Team
 * @version 2.0
 * @since 2.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/v2/room", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomV2Controller {
    
    /**
     * 房间服务接口，处理房间相关的业务逻辑
     */
    @Resource
    private IRoomV2Service roomService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RoomListV2Repository roomListV2Repository;

    /**
     * 创建房间
     * 
     * <p>根据请求参数创建一个新房间，支持自定义房间配置和业务负载数据。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/create</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>功能特性：</strong></p>
     * <ul>
     *   <li>自动生成唯一房间ID</li>
     *   <li>支持场景类型配置</li>
     *   <li>可携带自定义负载数据</li>
     *   <li>自动记录创建时间戳</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "appId": "your_app_id",
     *   "sceneId": "live_streaming",
     *   "payload": {
     *     "roomName": "直播间001",
     *     "maxUsers": 1000
     *   }
     * }
     * </pre>
     * 
     * @param roomCreateReq 房间创建请求对象，包含创建房间所需的参数
     * @return 统一响应对象，成功时包含新创建的房间信息
     * @throws Exception 当创建过程中出现异常时抛出
     */
    @PostMapping("/create")
    @ResponseBody
    public R<RoomDto> create(@Validated @RequestBody RoomCreateReq roomCreateReq) throws Exception {
        RoomCreateDto roomCreateDto = roomService.create(roomCreateReq);
        return R.success(new RoomDto()
                .setAppId(roomCreateDto.getAppId())
                .setSceneId(roomCreateDto.getSceneId())
                .setRoomId(roomCreateDto.getRoomId())
                .setCreateTime(roomCreateDto.getCreateTime())
                .setUpdateTime(roomCreateDto.getUpdateTime())
                .setPayload(roomCreateDto.getPayload())
                .setGroupId(roomCreateDto.getGroupId())
                .setStatus(roomCreateDto.getStatus())
        );  
    }

    /**
     * 简化版创建会议室
     * 
     * <p>前端只需要传递群ID和群主ID，其他配置由后端自动生成。</p>
     * <p>包括：appId、sceneId、roomId、payload、聊天室配置、IM配置等。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/createSimple</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>功能特性：</strong></p>
     * <ul>
     *   <li>自动生成唯一房间ID</li>
     *   <li>自动配置聊天室参数</li>
     *   <li>自动设置IM配置</li>
     *   <li>支持自定义会议室名称</li>
     *   <li>支持设置最大用户数</li>
     *   <li>自动记录创建时间戳</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "groupId": "group_12345",
     *   "ownerId": "user_123",
     *   "type": 0,
     *   "roomName": "我的会议室"
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "appId": "d1f140de133c4508a532c0033840a801",
     *     "sceneId": "live_streaming",
     *     "roomId": "group_group_12345_room_1694073000000_1234",
     *     "groupId": "group_12345",
     *     "ownerId": "user_123",
     *     "status": "active",
     *     "payload": {
     *       "roomName": "我的会议室",
     *       "description": "群聊会议室",
     *       "maxUsers": 1000000,
     *       "type": 0,
     *       "user": {
     *         "username": "user_123"
     *       },
     *       "allMic": true,
     *       "allMute": false,
     *       "chatRoomConfig": {
     *         "maxUsers": 1000000,
     *         "name": "我的会议室"
     *       },
     *       "imConfig": null
     *     },
     *     "createTime": 1694073000000,
     *     "updateTime": 1694073000000,
     *     "chatRoomConfig": {
     *       "maxUsers": 1000,
     *       "name": "我的会议室"
     *     },
     *     "imConfig": null
     *   },
     *   "success": true
     * }
     * </pre>
     * 
     * @param simpleRoomCreateReq 简化版会议室创建请求对象，包含群ID和群主ID
     * @return 统一响应对象，成功时包含新创建的会议室完整信息
     * @throws Exception 当创建过程中出现异常时抛出
     */
    @PostMapping("/createSimple")
    @ResponseBody
    public AjaxJson createSimple(@Validated @RequestBody SimpleRoomCreateReq simpleRoomCreateReq) throws Exception {
        SimpleRoomCreateDto simpleRoomCreateDto = roomService.createSimpleRoom(simpleRoomCreateReq);
        Map<String, Object> result = BeanUtil.beanToMap(simpleRoomCreateDto, false, true);
        return AjaxJson.success().setData(result);
    }

    /**
     * 仅根据群ID生成会议室ID
     * 
     * <p>前端只传递群ID，本接口返回生成的roomId，不创建任何数据。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/roomId/generate</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * { "groupId": "group_12345" }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * { "code":200, "message":"success", "data": { "roomId": "group_group_12345_room_1694073000000_1234" } }
     * </pre>
     * 
     * @param body 请求体，包含groupId
     * @return 仅包含生成的roomId
     */
    @PostMapping("/getUniqueRoomId")
    @ResponseBody
    public AjaxJson generateRoomId(@RequestBody Map<String, String> body) {
        String groupId = body == null ? null : body.get("groupId");
        String mid = body == null ? null : body.get("mid");
        if (groupId == null || groupId.trim().isEmpty()) {
            return AjaxJson.error("groupId不能为空");
        }

        // 1) 检查群ID是否已有会议室记录（排除已销毁的会议）
        List<RoomListV2Entity> existingRooms = roomListV2Repository.findByGroupId(groupId);
        if (existingRooms != null && !existingRooms.isEmpty()) {
            log.info("群ID {} 找到 {} 个会议记录", groupId, existingRooms.size());
            
            // 过滤掉已销毁的会议，只保留活跃状态的会议
            List<RoomListV2Entity> activeRooms = existingRooms.stream()
                    .filter(room -> !"destroyed".equals(room.getStatus()))
                    .collect(Collectors.toList());
            
            log.info("群ID {} 过滤后剩余 {} 个活跃会议记录", groupId, activeRooms.size());
            
            if (!activeRooms.isEmpty()) {
                // 获取最新的一条活跃会议记录
                RoomListV2Entity latest = activeRooms.stream()
                        .max(Comparator.comparingLong(RoomListV2Entity::getUpdateTime))
                        .orElse(activeRooms.get(0));
                
                log.info("群ID {} 返回活跃会议记录: roomId={}, status={}", groupId, latest.getRoomId(), latest.getStatus());
                
                Map<String, Object> existed = new HashMap<>();
                existed.put("roomId", latest.getRoomId());
                existed.put("groupId", latest.getGroupId());
                existed.put("status", latest.getStatus());
                existed.put("exists", 1); // 1代表已存在会议记录
                
                AjaxJson res = AjaxJson.success();
                res.setMsg("已存在会议记录");
                res.setData(existed);
                return res;
            } else {
                log.info("群ID {} 所有会议都已销毁，将创建新会议", groupId);
            }
            // 如果所有会议都已销毁，继续执行创建新会议的逻辑
        }

        // 并发防重：短期缓存"创建中"状态，避免3-5秒落库延迟期间重复生成
        String inflightKey = "room:create:inflight:" + groupId + (mid == null ? "" : (":" + mid));
        String inflightVal = redisUtil.get(inflightKey);
        if (inflightVal != null) {
            long ttl = redisUtil.getExpire(inflightKey);
            String msg = ttl > 0 ? ("会议正在创建中，请" + ttl + "秒后重试") : "会议正在创建中，请稍后重试";
            Map<String, Object> inflight = new HashMap<>();
            inflight.put("exists", 0); // 0代表当前没有进行中的会议（但创建中）
            if (ttl > 0) {
                inflight.put("retryAfter", ttl);
            }
            AjaxJson res = AjaxJson.error(msg);
            res.setData(inflight);
            return res;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf(ThreadLocalRandom.current().nextInt(10000, 100000));
        String roomId = "group_" + timestamp + "_" + random;
//        String roomId = "group_" + groupId + "_room_" + timestamp + "_" + random;

        // 写入短期缓存（7秒），防止短时间内重复生成
        redisUtil.set(inflightKey, roomId, 7);

        // 建立roomId与群ID的映射关系，存储到Redis缓存中（24小时过期）
        String mappingKey = "room:group:mapping:" + roomId;
        redisUtil.set(mappingKey, groupId, 24 * 60 * 60); // 24小时过期
        
        // 将群ID和会议室ID的关联关系存储到现有的RoomListV2Entity中
        try {
            RoomListV2Entity roomEntity = new RoomListV2Entity();
            roomEntity.setRoomId(roomId);
            roomEntity.setGroupId(groupId);
            roomEntity.setStatus("待创建"); // 默认状态为待创建
            roomEntity.setCreateTime(System.currentTimeMillis());
            roomEntity.setUpdateTime(System.currentTimeMillis());
            roomEntity.setOwnerId(mid); // 设置创建者ID
            
            // 设置基本payload信息
            Map<String, Object> payload = new HashMap<>();
            payload.put("roomName", "群会议");
            payload.put("type", 1); // 默认类型
            payload.put("status", "待创建"); // 在payload中也记录状态
            roomEntity.setPayload(payload);
            
            // 保存到MongoDB
            roomListV2Repository.save(roomEntity);
            
            // 记录日志
            System.out.println("成功创建群会议记录 - roomId: " + roomId + ", groupId: " + groupId + ", status: 待创建");
            
        } catch (Exception e) {
            // 记录错误但不影响接口返回
            System.err.println("保存群会议记录失败: " + e.getMessage());
             e.printStackTrace();
            // throw new RuntimeException("保存群会议记录失败", e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("roomId", roomId);
        result.put("exists", 0); // 0代表没有进行中的会议
        return AjaxJson.success().setData(result);
    }

    /**
     * 销毁房间
     * 
     * <p>根据房间ID销毁指定房间，释放相关资源。</p>
     * <p>销毁操作是不可逆的，请谨慎使用。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/destroy</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>操作影响：</strong></p>
     * <ul>
     *   <li>移除房间内所有用户</li>
     *   <li>清理房间相关数据</li>
     *   <li>释放系统资源</li>
     *   <li>停止房间相关服务</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "roomId": "room_123456"
     * }
     * </pre>
     * 
     * @param roomDestroyReq 房间销毁请求对象，包含要销毁的房间ID
     * @return 统一响应对象，成功时包含被销毁的房间ID
     * @throws Exception 当销毁过程中出现异常时抛出，如房间不存在、权限不足等
     */
    @PostMapping("/destroy")
    @ResponseBody
    public R<RoomDto> destroy(@Validated @RequestBody RoomDestroyReq roomDestroyReq) throws Exception {
        roomService.destroy(roomDestroyReq);
        return R.success(new RoomDto().setRoomId(roomDestroyReq.getRoomId()));
    }

    /**
     * 更新房间信息
     * 
     * <p>动态更新房间的配置信息和负载数据。</p>
     * <p>支持增量更新，只更新提供的字段。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/update</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>可更新内容：</strong></p>
     * <ul>
     *   <li>房间名称和描述</li>
     *   <li>房间配置参数</li>
     *   <li>自定义负载数据</li>
     *   <li>房间状态标识</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "roomId": "room_123456",
     *   "payload": {
     *     "roomName": "更新后的房间名",
     *     "description": "房间描述更新"
     *   }
     * }
     * </pre>
     * 
     * @param updateReq 房间更新请求对象，包含房间ID和要更新的字段
     * @return 统一响应对象，更新成功时返回success状态
     * @throws Exception 当更新过程中出现异常时抛出，如房间不存在、参数无效等
     */
    @PostMapping("/update")
    @ResponseBody
    public R<Void> update(@Validated @RequestBody RoomUpdateReq updateReq) throws Exception {
        roomService.update(updateReq);
        return R.success(null);
    }

    /**
     * 获取房间列表
     * 
     * <p>分页获取房间列表，支持基于时间的分页查询。</p>
     * <p>使用时间戳游标实现高效分页，适合大数据量场景。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/list</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>分页机制：</strong></p>
     * <ul>
     *   <li>基于创建时间的游标分页</li>
     *   <li>支持指定页面大小</li>
     *   <li>自动处理时间边界</li>
     *   <li>返回下次查询的游标</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "appId": "your_app_id",
     *   "sceneId": "live_streaming",
     *   "pageSize": 20,
     *   "lastCreateTime": 1694073000000
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "data": {
     *     "total": 100,
     *     "pageSize": 20,
     *     "list": [房间列表],
     *     "nextCursor": 1694072000000
     *   }
     * }
     * </pre>
     * 
     * @param roomListReq 房间列表请求对象，包含查询条件和分页参数
     * @return 统一响应对象，包含分页的房间列表数据
     * @throws Exception 当查询过程中出现异常时抛出
     */
    @PostMapping("/list")
    @ResponseBody
    public R<RoomListDto<RoomListEntity>> list(@Validated @RequestBody RoomListReq roomListReq) throws Exception {
        if (roomListReq.getLastCreateTime() == null || roomListReq.getLastCreateTime() == 0) {
            roomListReq.setLastCreateTime(System.currentTimeMillis());
        }
        RoomListDto<RoomListEntity> roomList = roomService.getRoomList(roomListReq);
        return R.success(roomList);
    }

    /**
     * 查询房间详情
     *
     * <p>根据房间ID获取房间的详细信息，包括配置、状态、统计数据等。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/query</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>返回信息：</strong></p>
     * <ul>
     *   <li>房间基本信息（ID、名称、描述）</li>
     *   <li>房间配置参数</li>
     *   <li>当前状态信息</li>
     *   <li>用户统计数据</li>
     *   <li>创建和更新时间</li>
     *   <li>自定义负载数据</li>
     * </ul>
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
     *   "data": {
     *     "roomId": "room_123456",
     *     "appId": "your_app_id",
     *     "sceneId": "live_streaming",
     *     "status": "active",
     *     "userCount": 150,
     *     "payload": {...},
     *     "createTime": 1694073000000,
     *     "updateTime": 1694073500000
     *   }
     * }
     * </pre>
     * 
     * @param roomQueryReq 房间查询请求对象，包含要查询的房间ID
     * @return 统一响应对象，包含房间的详细信息
     * @throws Exception 当查询过程中出现异常时抛出，如房间不存在等
     */
    @PostMapping("/query")
    @ResponseBody
    public R<RoomQueryDto>  query(@Validated @RequestBody RoomQueryReq roomQueryReq) throws Exception {
        RoomQueryDto roomQueryDto = roomService.query(roomQueryReq);
        return R.success(roomQueryDto);
    }

    // ==================== 房间状态管理接口 ====================

    /**
     * 更新房间状态
     * 
     * <p>更新指定房间的状态，支持状态转换验证。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/status/update</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>支持的状态：</strong></p>
     * <ul>
     *   <li><strong>pending_create：</strong>待创建</li>
     *   <li><strong>active：</strong>房间活跃状态，有用户在线</li>
     *   <li><strong>inactive：</strong>房间非活跃状态，无用户在线</li>
     *   <li><strong>destroyed：</strong>房间已销毁</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "appId": "your_app_id",
     *   "sceneId": "live_streaming",
     *   "roomId": "room_123456",
     *   "status": "active"
     * }
     * </pre>
     * 
     * @param request 状态更新请求对象
     * @return 统一响应对象
     * @throws Exception 当更新过程中出现异常时抛出
     */
    @PostMapping("/status/update")
    @ResponseBody
    public R<Void> updateRoomStatus(@Validated @RequestBody RoomStatusUpdateReq request) throws Exception {
        roomService.updateRoomStatus(request.getAppId(), request.getSceneId(), request.getRoomId(), request.getStatus());
        return R.success(null);
    }

    /**
     * 用户加入房间
     * 
     * <p>当用户加入房间时，将房间状态更新为活跃状态。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/user/join</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * @param request 用户加入房间请求对象
     * @return 统一响应对象
     * @throws Exception 当更新过程中出现异常时抛出
     */
    @PostMapping("/user/join")
    @ResponseBody
    public R<Void> userJoinRoom(@Validated @RequestBody RoomUserActionReq request) throws Exception {
        roomService.userJoinRoom(request.getAppId(), request.getSceneId(), request.getRoomId());
        return R.success(null);
    }

    /**
     * 用户离开房间
     * 
     * <p>当用户离开房间时，将房间状态更新为非活跃状态。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/user/leave</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * @param request 用户离开房间请求对象
     * @return 统一响应对象
     * @throws Exception 当更新过程中出现异常时抛出
     */
    @PostMapping("/user/leave")
    @ResponseBody
    public R<Void> userLeaveRoom(@Validated @RequestBody RoomUserActionReq request) throws Exception {
        roomService.userLeaveRoom(request.getAppId(), request.getSceneId(), request.getRoomId());
        return R.success(null);
    }

    /**
     * 获取房间状态
     * 
     * <p>查询指定房间的当前状态。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/status/get</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "status": "active"
     *   }
     * }
     * </pre>
     * 
     * @param request 房间状态查询请求对象
     * @return 统一响应对象，包含房间当前状态
     * @throws Exception 当查询过程中出现异常时抛出
     */
    @PostMapping("/status/get")
    @ResponseBody
    public R<RoomStatusResponse> getRoomStatus(@Validated @RequestBody RoomStatusQueryReq request) throws Exception {
        String status = roomService.getRoomStatus(request.getAppId(), request.getSceneId(), request.getRoomId());
        return R.success(new RoomStatusResponse().setStatus(status));
    }

    // ==================== 群会议查询接口 ====================

    /**
     * 查询群内正在进行的会议
     * 
     * <p>根据群ID查询该群内所有活跃状态的会议信息。</p>
     * <p>用于群聊界面显示是否有正在进行的会议，用户可点击直接进入。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/group/meetings</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>功能特性：</strong></p>
     * <ul>
     *   <li>只返回活跃状态(active)的会议</li>
     *   <li>支持按场景ID筛选</li>
     *   <li>返回会议详细信息供UI显示</li>
     *   <li>提供会议入口信息</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "appId": "your_app_id",
     *   "groupId": "group_12345",
     *   "sceneId": "live_streaming"
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "groupId": "group_12345",
     *     "hasActiveMeeting": true,
     *     "activeMeetingCount": 1,
     *     "activeMeetings": [
     *       {
     *         "appId": "your_app_id",
     *         "sceneId": "live_streaming",
     *         "roomId": "room_001",
     *         "groupId": "group_12345",
     *         "status": "active",
     *         "payload": {
     *           "roomName": "群会议",
     *           "description": "正在进行的群会议"
     *         },
     *         "createTime": 1694073000000,
     *         "updateTime": 1694073500000,
     *         "lastActiveTime": 1694073500000,
     *         "ownerId": "user_123"
     *       }
     *     ]
     *   }
     * }
     * </pre>
     *
     * @param request 群会议查询请求对象
     * @return 统一响应对象，包含群内活跃会议信息
     * @throws Exception 当查询过程中出现异常时抛出
     */
    @PostMapping("/group/meetings")
    @ResponseBody
    public AjaxJson getGroupActiveMeetings(@Validated @RequestBody GroupMeetingQueryReq request) throws Exception {
        GroupMeetingResponseDto response = roomService.getGroupActiveMeetings(
                request.getAppId(), 
                request.getGroupId(), 
                request.getSceneId()
        );
        Map<String, Object> result = BeanUtil.beanToMap(response, false, true);
        return AjaxJson.success().setData(result);
    }
    
    /**
     * 根据会议室ID查询会议室详情
     * 
     * @param roomId 会议室ID
     * @return 统一响应对象，包含会议室详情信息
     * @throws Exception 当查询过程中出现异常时抛出
     */
    @GetMapping("/detail/{roomId}")
    @ResponseBody
    public AjaxJson getRoomDetail(@PathVariable String roomId) throws Exception {
        RoomDetailDto roomDetail = roomService.getRoomDetail(roomId);
        Map<String, Object> result = BeanUtil.beanToMap(roomDetail, false, true);
        return AjaxJson.success().setData(result);
    }
    
    /**
     * 更新会议室设置
     * 
     * <p>更新会议室的设置信息，包括全员开麦、全员禁言和会议室状态。</p>
     * 
     * <p><strong>HTTP方法：</strong>POST</p>
     * <p><strong>请求路径：</strong>/v2/room/settings/update</p>
     * <p><strong>Content-Type：</strong>application/json</p>
     * 
     * <p><strong>支持的状态：</strong></p>
     * <ul>
     *   <li><strong>pending_create：</strong>待创建</li>
     *   <li><strong>active：</strong>房间活跃状态，有用户在线</li>
     *   <li><strong>inactive：</strong>房间非活跃状态，无用户在线</li>
     *   <li><strong>destroyed：</strong>房间已销毁</li>
     * </ul>
     * 
     * <p><strong>请求体示例：</strong></p>
     * <pre>
     * {
     *   "roomId": "room_123456",
     *   "allMic": true,
     *   "allMute": false,
     *   "status": "active"
     * }
     * </pre>
     * 
     * <p><strong>响应示例：</strong></p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "success": true
     *   }
     * }
     * </pre>
     * 
     * @param request 更新请求对象
     * @return 统一响应对象，包含更新结果
     * @throws Exception 当更新过程中出现异常时抛出
     */
    @PostMapping("/settings/update")
    @ResponseBody
    public AjaxJson updateRoomSettings(@Validated @RequestBody RoomSettingsUpdateReq request) throws Exception {
        boolean success = roomService.updateRoomSettings(request);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return AjaxJson.success().setData(result);
    }
}