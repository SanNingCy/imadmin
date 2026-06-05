package com.seekweb4.chat.agora.roomadmin.service.impl;

import com.seekweb4.chat.agora.bean.config.ChatRoomConfig;
import com.seekweb4.chat.agora.bean.entity.RoomListV2Entity;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.roomadmin.bean.*;
import com.seekweb4.chat.agora.roomadmin.service.RoomAdminService;
import com.seekweb4.chat.agora.config.WhitelistConfig;
import com.seekweb4.chat.agora.service.IChatRoomService;
import com.seekweb4.chat.agora.service.IRoomV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * 会议室后台管理服务实现类
 * 
 * @author Admin Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
public class RoomAdminServiceImpl implements RoomAdminService {

    @Autowired
    private RoomListV2Repository roomListV2Repository;

    @Autowired
    private IRoomV2Service roomV2Service;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WhitelistConfig whiteListConfig;

    @Autowired
    private IChatRoomService iChatRoomService;

    @Value("${chatRoom.domain:}")
    private String chatRoomDomain;

    @Value("${whitelist.token.appId:}")
    private String defaultAppId;

    @Value("${whitelist.chatRoom.orgName:}")
    private String defaultOrgName;

    @Value("${whitelist.chatRoom.appName:}")
    private String defaultAppName;

    @Value("${whitelist.chatRoom.clientId:}")
    private String defaultClientId;

    @Value("${whitelist.chatRoom.clientSecret:}")
    private String defaultClientSecret;

    @Override
    public RoomListResponse getRoomList(RoomListQueryReq request) throws Exception {
        log.info("开始查询会议列表，查询条件：{}", request);

        // 构建查询条件
        Query query = buildQuery(request);
        
        // 构建排序
        Sort sort = buildSort(request);
        query.with(sort);

        // 构建分页
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize());
        query.with(pageable);

        // 执行查询
        List<RoomListV2Entity> entities = mongoTemplate.find(query, RoomListV2Entity.class);
        
        // 查询总数
        Query countQuery = buildQuery(request);
        long total = mongoTemplate.count(countQuery, RoomListV2Entity.class);

        // 转换为响应对象
        List<RoomListResponse.RoomInfo> roomList = entities.stream()
                .map(this::convertToRoomInfo)
                .collect(Collectors.toList());

        // 构建响应
        RoomListResponse response = new RoomListResponse();
        response.setTotal(total);
        response.setPageNum(request.getPageNum());
        response.setPageSize(request.getPageSize());
        response.setPages((int) Math.ceil((double) total / request.getPageSize()));
        response.setList(roomList);

        log.info("查询会议列表完成，总数：{}，当前页：{}，每页大小：{}", total, request.getPageNum(), request.getPageSize());
        return response;
    }

    @Override
    public RoomDetailResponse getChatRoomDetail(RoomDetailQueryReq request) throws Exception {
        log.info("开始查询会议聊天室详情，roomId：{}", request.getRoomId());

        // 查询会议室信息
        Optional<RoomListV2Entity> roomOpt = roomListV2Repository.findByRoomId(request.getRoomId());
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("会议室不存在：" + request.getRoomId());
        }

        RoomListV2Entity entity = roomOpt.get();
        
        // 转换为响应对象
        RoomDetailResponse response = convertToRoomDetail(entity);

        log.info("查询会议聊天室详情完成，roomId：{}", request.getRoomId());
        return response;
    }

    @Override
    public Map<String, Object> destroyRoom(RoomDestroyReq request) throws Exception {
        log.info("开始解散会议，roomId：{}，操作者：{}", request.getRoomId(), request.getOperatorId());

        // 检查会议室是否存在
        Optional<RoomListV2Entity> roomOpt = roomListV2Repository.findByRoomId(request.getRoomId());
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("会议室不存在：" + request.getRoomId());
        }

        RoomListV2Entity entity = roomOpt.get();
        
        // 检查会议室状态
        if ("destroyed".equals(entity.getStatus())) {
            throw new RuntimeException("会议室已经解散：" + request.getRoomId());
        }

        // 调用声网API解散会议室
        com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq destroyReq = new com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq();
        destroyReq.setAppId(entity.getAppId());
        destroyReq.setSceneId(entity.getSceneId());
        destroyReq.setRoomId(entity.getRoomId());
        
        try {
            roomV2Service.destroy(destroyReq);
            log.info("声网API解散会议室成功，roomId：{}", request.getRoomId());
        } catch (Exception e) {
            log.error("声网API解散会议室失败，roomId：{}", request.getRoomId(), e);
            // 即使声网API失败，也要更新本地状态
        }

        // 更新本地状态
        entity.setStatus("destroyed");
        entity.setUpdateTime(System.currentTimeMillis());
        roomListV2Repository.save(entity);

        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("roomId", request.getRoomId());
        result.put("destroyTime", System.currentTimeMillis());
        result.put("operatorId", request.getOperatorId());
        result.put("reason", request.getReason());

        log.info("解散会议完成，roomId：{}", request.getRoomId());
        return result;
    }

    @Override
    public Map<String, Object> batchDestroyRooms(Map<String, Object> request) throws Exception {
        log.info("开始批量解散会议，请求：{}", request);

        @SuppressWarnings("unchecked")
        List<String> roomIds = (List<String>) request.get("roomIds");
        String reason = (String) request.get("reason");
        String operatorId = (String) request.get("operatorId");

        if (roomIds == null || roomIds.isEmpty()) {
            throw new RuntimeException("房间ID列表不能为空");
        }

        List<Map<String, Object>> results = new ArrayList<>();
        int successCount = 0;
        int failedCount = 0;

        for (String roomId : roomIds) {
            try {
                RoomDestroyReq destroyReq = new RoomDestroyReq();
                destroyReq.setRoomId(roomId);
                destroyReq.setReason(reason);
                destroyReq.setOperatorId(operatorId);
                
                destroyRoom(destroyReq);
                
                Map<String, Object> result = new HashMap<>();
                result.put("roomId", roomId);
                result.put("success", true);
                results.add(result);
                successCount++;
                
            } catch (Exception e) {
                log.error("解散会议失败，roomId：{}", roomId, e);
                
                Map<String, Object> result = new HashMap<>();
                result.put("roomId", roomId);
                result.put("success", false);
                result.put("error", e.getMessage());
                results.add(result);
                failedCount++;
            }
        }

        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("total", roomIds.size());
        response.put("success", successCount);
        response.put("failed", failedCount);
        response.put("results", results);

        log.info("批量解散会议完成，总数：{}，成功：{}，失败：{}", roomIds.size(), successCount, failedCount);
        return response;
    }

    @Override
    public List<Map<String, Object>> getRoomUsers(String roomId) throws Exception {
        log.info("开始获取会议室用户列表，roomId：{}", roomId);

        if (!StringUtils.hasText(roomId)) {
            throw new RuntimeException("会议室ID不能为空");
        }

        Optional<RoomListV2Entity> roomOpt = roomListV2Repository.findByRoomId(roomId);
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("会议室不存在：" + roomId);
        }

        RoomListV2Entity entity = roomOpt.get();
        String chatRoomId = entity.getChatRoomId();
        if (!StringUtils.hasText(chatRoomId)) {
            throw new RuntimeException("会议室未绑定聊天室ID");
        }

        // 解析 appId（优先取实体，其次全局默认）
        String appId = entity.getAppId();
        if (!StringUtils.hasText(appId)) {
            appId = defaultAppId;
        }

        // 从白名单配置获取环信/声网Chat参数
        ChatRoomConfig cfg = null;
        if (StringUtils.hasText(appId)) {
            cfg = whiteListConfig.getChatRoomFromWhitelist(appId);
        }
        // 如果白名单未配置，尝试使用全局默认参数兜底
        if (cfg == null && StringUtils.hasText(defaultOrgName) && StringUtils.hasText(defaultAppName)
                && StringUtils.hasText(defaultClientId) && StringUtils.hasText(defaultClientSecret)) {
            ChatRoomConfig fallback = new ChatRoomConfig();
            // 逐个setter，避免链式调用（返回void）导致“无法取消引用void”
            fallback.setOrgName(defaultOrgName);
            fallback.setAppName(defaultAppName);
            fallback.setClientId(defaultClientId);
            fallback.setClientSecret(defaultClientSecret);
            fallback.setAppId(appId);
            cfg = fallback;
        }
        if (cfg == null) {
            throw new RuntimeException("未找到AppId对应的聊天配置: " + appId);
        }

        String orgName = cfg.getOrgName();
        String appName = cfg.getAppName();
        String clientId = cfg.getClientId();
        String clientSecret = cfg.getClientSecret();

        // 获取应用token，并调用聊天室成员列表接口
        String appToken = iChatRoomService.getAppToken(orgName, appName, clientId, clientSecret);
        String authorization = "Bearer " + appToken;

        String base = chatRoomDomain;
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String url = base + "/" + orgName + "/" + appName + "/chatrooms/" + chatRoomId + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(resp.getBody());

        // 兼容常见返回结构：
        // 1) {"data": [ {"member":"u"}, ... ]}
        // 2) {"data": {"members": ["u1","u2"]}}
        // 3) {"entities": [ {"uuid":"...","username":"..."} ]}
        List<Map<String, Object>> users = new ArrayList<>();
        JsonNode dataNode = root.get("data");
        if (dataNode != null) {
            if (dataNode.isArray()) {
                for (JsonNode item : dataNode) {
                    Map<String, Object> u = new HashMap<>();
                    if (item.has("member")) {
                        u.put("username", item.get("member").asText());
                    }
                    if (item.has("username")) {
                        u.put("username", item.get("username").asText());
                    }
                    if (item.has("uuid")) {
                        u.put("userId", item.get("uuid").asText());
                    }
                    if (item.has("role")) {
                        u.put("role", item.get("role").asText());
                    }
                    if (!u.isEmpty()) {
                        users.add(u);
                    }
                }
            } else if (dataNode.isObject()) {
                JsonNode members = dataNode.get("members");
                if (members != null && members.isArray()) {
                    for (JsonNode m : members) {
                        Map<String, Object> u = new HashMap<>();
                        // 数组可能是字符串或对象
                        if (m.isTextual()) {
                            u.put("username", m.asText());
                        } else if (m.isObject()) {
                            if (m.has("username")) {
                                u.put("username", m.get("username").asText());
                            }
                            if (m.has("uuid")) {
                                u.put("userId", m.get("uuid").asText());
                            }
                        }
                        if (!u.isEmpty()) {
                            users.add(u);
                        }
                    }
                }
            }
        }
        if (users.isEmpty()) {
            JsonNode entities = root.get("entities");
            if (entities != null && entities.isArray()) {
                for (JsonNode item : entities) {
                    Map<String, Object> u = new HashMap<>();
                    if (item.has("username")) {
                        u.put("username", item.get("username").asText());
                    }
                    if (item.has("uuid")) {
                        u.put("userId", item.get("uuid").asText());
                    }
                    if (!u.isEmpty()) {
                        users.add(u);
                    }
                }
            }
        }

        log.info("获取会议室用户列表完成，roomId：{}，用户数：{}", roomId, users.size());
        return users;
    }

    @Override
    public Map<String, Object> getRoomStats(Long startTime, Long endTime, String groupId) throws Exception {
        log.info("开始获取会议室统计数据，startTime：{}，endTime：{}，groupId：{}", startTime, endTime, groupId);
        // 基础条件（时间范围 + groupId）
        Criteria base = buildBaseCriteria(startTime, endTime, groupId);

        // 总数
        long totalRooms = mongoTemplate.count(Query.query(base), RoomListV2Entity.class);

        // 活跃
        Criteria activeCriteria = new Criteria().andOperator(base, Criteria.where("status").is("active"));
        long activeRooms = mongoTemplate.count(Query.query(activeCriteria), RoomListV2Entity.class);

        // 已销毁
        Criteria destroyedCriteria = new Criteria().andOperator(base, Criteria.where("status").is("destroyed"));
        long destroyedRooms = mongoTemplate.count(Query.query(destroyedCriteria), RoomListV2Entity.class);

        // 今日新增（忽略原有startTime，使用todayStart覆盖；保留groupId）
        long todayStart = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        Criteria todayBase = buildBaseCriteria(todayStart, null, groupId);
        long todayCreated = mongoTemplate.count(Query.query(todayBase), RoomListV2Entity.class);

        Map<String, Object> result = new HashMap<>();
        result.put("totalRooms", totalRooms);
        result.put("activeRooms", activeRooms);
        result.put("destroyedRooms", destroyedRooms);
        result.put("todayCreated", todayCreated);
        result.put("queryTime", System.currentTimeMillis());

        log.info("获取会议室统计数据完成：{}", result);
        return result;
    }

    private Criteria buildBaseCriteria(Long startTime, Long endTime, String groupId) {
        List<Criteria> conditions = new ArrayList<>();
        if (startTime != null || endTime != null) {
            if (startTime != null && endTime != null) {
                conditions.add(Criteria.where("createTime").gte(startTime).lte(endTime));
            } else if (startTime != null) {
                conditions.add(Criteria.where("createTime").gte(startTime));
            } else {
                conditions.add(Criteria.where("createTime").lte(endTime));
            }
        }
        if (StringUtils.hasText(groupId)) {
            conditions.add(Criteria.where("groupId").is(groupId));
        }
        if (conditions.isEmpty()) {
            return new Criteria();
        }
        return new Criteria().andOperator(conditions.toArray(new Criteria[0]));
    }

    /**
     * 构建查询条件
     */
    private Query buildQuery(RoomListQueryReq request) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        // 房间ID精确匹配
        if (StringUtils.hasText(request.getRoomId())) {
            criteria.and("roomId").is(request.getRoomId());
        }

        // 群ID精确匹配
        if (StringUtils.hasText(request.getGroupId())) {
            criteria.and("groupId").is(request.getGroupId());
        }

        // 状态筛选
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            criteria.and("status").in(request.getStatus());
        }

        // 创建者ID精确匹配
        if (StringUtils.hasText(request.getOwnerId())) {
            criteria.and("ownerId").is(request.getOwnerId());
        }

        // 应用ID
        if (StringUtils.hasText(request.getAppId())) {
            criteria.and("appId").is(request.getAppId());
        }

        // 场景ID
        if (StringUtils.hasText(request.getSceneId())) {
            criteria.and("sceneId").is(request.getSceneId());
        }

        // 时间范围筛选
        if (request.getStartTime() != null) {
            criteria.and("createTime").gte(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            criteria.and("createTime").lte(request.getEndTime());
        }

        // 房间名称模糊匹配
        if (StringUtils.hasText(request.getRoomName())) {
            criteria.orOperator(
                Criteria.where("meetingName").regex(request.getRoomName(), "i"),
                Criteria.where("payload.roomName").regex(request.getRoomName(), "i")
            );
        }

        query.addCriteria(criteria);
        return query;
    }

    /**
     * 构建排序
     */
    private Sort buildSort(RoomListQueryReq request) {
        String orderBy = request.getOrderBy();
        String orderDirection = request.getOrderDirection();

        Sort.Direction direction = "desc".equalsIgnoreCase(orderDirection) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;

        switch (orderBy) {
            case "updateTime":
                return Sort.by(direction, "updateTime");
            case "lastActiveTime":
                return Sort.by(direction, "lastActiveTime");
            case "roomId":
                return Sort.by(direction, "roomId");
            case "createTime":
            default:
                return Sort.by(direction, "createTime");
        }
    }

    /**
     * 转换为房间信息
     */
    private RoomListResponse.RoomInfo convertToRoomInfo(RoomListV2Entity entity) {
        RoomListResponse.RoomInfo info = new RoomListResponse.RoomInfo();
        info.setRoomId(entity.getRoomId());
        info.setGroupId(entity.getGroupId());
        info.setOwnerId(entity.getOwnerId());
        info.setMeetingName(entity.getMeetingName());
        info.setStatus(entity.getStatus());
        info.setAppId(entity.getAppId());
        info.setSceneId(entity.getSceneId());
        info.setChatRoomId(entity.getChatRoomId());
        info.setCreateTime(entity.getCreateTime());
        info.setUpdateTime(entity.getUpdateTime());
        info.setLastActiveTime(entity.getLastActiveTime());
        info.setPayload(entity.getPayload());

        // 从payload中提取房间名称
        if (entity.getPayload() != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) entity.getPayload();
            if (payload.containsKey("roomName")) {
                info.setRoomName((String) payload.get("roomName"));
            }
            if (payload.containsKey("maxUsers")) {
                info.setMaxUsers((Integer) payload.get("maxUsers"));
            }
        }

        // 设置是否被封禁（这里可以根据实际业务逻辑判断）
        info.setIsBanned(false);

        return info;
    }

    /**
     * 转换为房间详情
     */
    private RoomDetailResponse convertToRoomDetail(RoomListV2Entity entity) {
        RoomDetailResponse response = new RoomDetailResponse();
        response.setRoomId(entity.getRoomId());
        response.setGroupId(entity.getGroupId());
        response.setOwnerId(entity.getOwnerId());
        response.setMeetingName(entity.getMeetingName());
        response.setStatus(entity.getStatus());
        response.setAppId(entity.getAppId());
        response.setSceneId(entity.getSceneId());
        response.setChatRoomId(entity.getChatRoomId());
        response.setCreateTime(entity.getCreateTime());
        response.setUpdateTime(entity.getUpdateTime());
        response.setLastActiveTime(entity.getLastActiveTime());
        response.setPayload(entity.getPayload());

        // 从payload中提取详细信息
        if (entity.getPayload() != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) entity.getPayload();
            
            if (payload.containsKey("roomName")) {
                response.setRoomName((String) payload.get("roomName"));
            }
            if (payload.containsKey("description")) {
                response.setDescription((String) payload.get("description"));
            }
            if (payload.containsKey("maxUsers")) {
                response.setMaxUsers((Integer) payload.get("maxUsers"));
            }
            if (payload.containsKey("allMic")) {
                response.setAllMic((Boolean) payload.get("allMic"));
            }
            if (payload.containsKey("allMute")) {
                response.setAllMute((Boolean) payload.get("allMute"));
            }

            // 聊天室配置
            if (payload.containsKey("chatRoomConfig")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> chatRoomConfig = (Map<String, Object>) payload.get("chatRoomConfig");
                RoomDetailResponse.ChatRoomConfig config = new RoomDetailResponse.ChatRoomConfig();
                if (chatRoomConfig.containsKey("maxUsers")) {
                    config.setMaxUsers((Integer) chatRoomConfig.get("maxUsers"));
                }
                if (chatRoomConfig.containsKey("name")) {
                    config.setName((String) chatRoomConfig.get("name"));
                }
                if (chatRoomConfig.containsKey("description")) {
                    config.setDescription((String) chatRoomConfig.get("description"));
                }
                response.setChatRoomConfig(config);
            }
        }

        // 设置是否被封禁（这里可以根据实际业务逻辑判断）
        response.setIsBanned(false);

        return response;
    }
}