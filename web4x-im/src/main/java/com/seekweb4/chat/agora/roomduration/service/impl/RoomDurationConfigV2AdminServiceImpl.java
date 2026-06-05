package com.seekweb4.chat.agora.roomduration.service.impl;

import com.seekweb4.chat.agora.roomadmin.bean.RoomDestroyReq;
import com.seekweb4.chat.agora.roomduration.entity.MeetingConfigV2Entity;
import com.seekweb4.chat.agora.roomduration.entity.MeetingEntity;
import com.seekweb4.chat.agora.roomduration.entity.dto.*;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingConfigV2PageResp;
import com.seekweb4.chat.agora.roomduration.repository.MeetingConfigV2Repository;
import com.seekweb4.chat.agora.repository.RoomListV2Repository;
import com.seekweb4.chat.agora.roomduration.service.IMeetingConfigV2AdminService;
import com.seekweb4.chat.agora.service.IRoomV2Service;
import com.seekweb4.chat.agora.bean.dto.v2.RoomDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 基于 RoomListV2Entity 的会议配置后台管理服务实现
 * 新增版本，不影响原有功能
 * 
 * @author Admin Team
 * @version 2.0
 * @since 2.0
 */
@Slf4j
@Service("roomDurationConfigV2AdminService")
public class RoomDurationConfigV2AdminServiceImpl implements IMeetingConfigV2AdminService {

    @Resource
    private MeetingConfigV2Repository repository;

    @Resource
    private RoomListV2Repository roomListV2Repository;

    @Resource
    private IRoomV2Service roomV2Service;

    private static final String DEFAULT_SCENE_ID = "live_streaming";

    @Override
    public MeetingConfigV2Dto getById(String id) {
        if (id == null) return null;
        return repository.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public String create(MeetingConfigV2CreateReq req) {
        MeetingConfigV2Entity e = new MeetingConfigV2Entity()
                .setAllMic(req.getAllMic())
                .setAllMute(req.getAllMute())
                .setStepConsumptionToken(req.getStepConsumptionToken())
                .setTimeZone(req.getTimeZone())
                .setUserTierOptions(req.getUserTierOptions())
                .setTimeOline(req.getTimeOline())
                .setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis());
        e = repository.save(e);
        return e.getId();
    }

    @Override
    public boolean update(MeetingConfigV2UpdateReq req) {
        if (req.getId() == null) return false;
        Optional<MeetingConfigV2Entity> opt = repository.findById(req.getId());
        if (!opt.isPresent()) return false;
        MeetingConfigV2Entity e = opt.get();
        if (req.getAllMic() != null) e.setAllMic(req.getAllMic());
        if (req.getAllMute() != null) e.setAllMute(req.getAllMute());
        if (req.getStepConsumptionToken() != null) e.setStepConsumptionToken(req.getStepConsumptionToken());
        if (req.getTimeZone() != null) e.setTimeZone(req.getTimeZone());
        if (req.getUserTierOptions() != null) e.setUserTierOptions(req.getUserTierOptions());
        if (req.getRenewalRules() != null) e.setRenewalRules(req.getRenewalRules());
        if (req.getTimeOline() != null) e.setTimeOline(req.getTimeOline());
        e.setUpdateTime(System.currentTimeMillis());
        repository.save(e);
        return true;
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) return false;
        repository.deleteById(id);
        return true;
    }

    @Override
    public Map<String, Object> destroyRoom(RoomDestroyReq request) throws Exception {
        log.info("开始解散会议（RoomListV2Entity版本），roomId：{}，操作者：{}", request.getRoomId(), request.getOperatorId());

        String roomId = request.getRoomId();
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("房间ID不能为空");
        }

        // 1) 优先通过声网查询房间详情，获取 appId/sceneId；查不到则使用默认兜底
        String appId = null;
        String sceneId = DEFAULT_SCENE_ID;
        
        try {
            RoomDetailDto roomDetail = roomV2Service.getRoomDetail(roomId);
            if (roomDetail != null) {
                appId = roomDetail.getAppId();
                sceneId = roomDetail.getSceneId() != null ? roomDetail.getSceneId() : DEFAULT_SCENE_ID;
                log.info("从声网获取到房间详情，appId：{}，sceneId：{}", appId, sceneId);
            }
        } catch (Exception e) {
            log.warn("从声网获取房间详情失败，将使用默认配置，roomId：{}", roomId, e);
        }

        // 如果无法获取到 appId，使用默认配置
        if (appId == null || appId.trim().isEmpty()) {
            // 这里可以从配置文件中读取默认的 appId
            appId = "default_app_id"; // 需要根据实际配置调整
            log.warn("使用默认 appId：{}，sceneId：{}", appId, sceneId);
        }

        // 2) 调用声网销毁
        com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq destroyReq = new com.seekweb4.chat.agora.bean.req.v2.RoomDestroyReq();
        destroyReq.setAppId(appId);
        destroyReq.setSceneId(sceneId);
        destroyReq.setRoomId(roomId);
        try {
            roomV2Service.destroy(destroyReq);
            log.info("声网API解散会议室成功，roomId：{}", roomId);
        } catch (Exception e) {
            log.warn("声网API解散会议室失败，roomId={}，将继续本地状态更新", roomId, e);
        }

        // 3) 更新本地 RoomListV2Entity 状态（若存在）
        try {
            java.util.Optional<com.seekweb4.chat.agora.bean.entity.RoomListV2Entity> roomOpt = roomListV2Repository.findByRoomId(roomId);
            if (roomOpt.isPresent()) {
                com.seekweb4.chat.agora.bean.entity.RoomListV2Entity entity = roomOpt.get();
                if (!"destroyed".equals(entity.getStatus())) {
                    entity.setStatus("destroyed");
                    entity.setUpdateTime(System.currentTimeMillis());
                    if (entity.getEndTime() == null) {
                        entity.setEndTime(System.currentTimeMillis());
                    }
                    roomListV2Repository.save(entity);
                    log.info("本地RoomListV2Entity状态更新成功，roomId={}", roomId);
                }
            } else {
                log.warn("未找到对应的RoomListV2Entity，roomId={}", roomId);
            }
        } catch (Exception e) {
            log.warn("更新本地RoomListV2Entity状态失败 roomId={}", roomId, e);
        }

        // 4) 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("roomId", roomId);
        result.put("destroyTime", System.currentTimeMillis());
        result.put("operatorId", request.getOperatorId());
        result.put("reason", request.getReason());
        result.put("version", "RoomListV2Entity");
        log.info("解散会议完成（RoomListV2Entity版本），roomId：{}", roomId);
        return result;
    }

    @Override
    public boolean insert(MeetingConfigV2UpdateReq req) {
        if (req.getId() == null) return false;
        MeetingConfigV2Entity e = repository.findById(req.getId()).orElse(null);
        if (e == null) return false;

        // 只允许新增 userTierOptions，不允许修改现有项
        if (req.getUserTierOptions() != null && !req.getUserTierOptions().isEmpty()) {
            List<Map<String, Object>> existing = e.getUserTierOptions();
            if (existing == null) {
                e.setUserTierOptions(req.getUserTierOptions());
            } else {
                // 只新增逻辑：检查是否存在相同value的项，如果存在则提示错误
                for (Map<String, Object> newItem : req.getUserTierOptions()) {
                    Object newValue = newItem.get("value");
                    Object newName = newItem.get("name");
                    if (newValue != null && newName != null) {
                        // 查找是否存在相同value的项
                        for (Map<String, Object> existingItem : existing) {
                            if (newValue.equals(existingItem.get("value"))) {
                                throw new IllegalArgumentException("用户等级选项已存在，value=" + newValue + "，name=" + existingItem.get("name") + "，不允许修改现有项");
                            }
                        }
                        // 如果不存在，则添加新项
                        existing.add(newItem);
                    }
                }
                e.setUserTierOptions(existing);
            }
        }

        // 只允许新增 timeOline，不允许修改现有项
        if (req.getTimeOline() != null && !req.getTimeOline().isEmpty()) {
            List<Map<String, Object>> existing = e.getTimeOline();
            if (existing == null) {
                e.setTimeOline(req.getTimeOline());
            } else {
                // 只新增逻辑：检查是否存在相同value的项，如果存在则提示错误
                for (Map<String, Object> newItem : req.getTimeOline()) {
                    Object newValue = newItem.get("value");
                    Object newName = newItem.get("name");
                    if (newValue != null && newName != null) {
                        // 查找是否存在相同value的项
                        for (Map<String, Object> existingItem : existing) {
                            if (newValue.equals(existingItem.get("value"))) {
                                throw new IllegalArgumentException("时长选项已存在，value=" + newValue + "，name=" + existingItem.get("name") + "，不允许修改现有项");
                            }
                        }
                        // 如果不存在，则添加新项
                        existing.add(newItem);
                    }
                }
                e.setTimeOline(existing);
            }
        }

        e.setUpdateTime(System.currentTimeMillis());
        repository.save(e);
        return true;
    }

    @Override
    public boolean reqBackspace(MeetingConfigV2UpdateReq req) {
        if (req.getId() == null) return false;
        MeetingConfigV2Entity e = repository.findById(req.getId()).orElse(null);
        if (e == null) return false;
//        if (req.getAllMic() != null) e.setAllMic(req.getAllMic());
//        if (req.getAllMute() != null) e.setAllMute(req.getAllMute());
//        if (req.getStepConsumptionToken() != null) {
//            e.setStepConsumptionToken(req.getStepConsumptionToken());
//            e.setRenewalRules("续费规则：每30分钟收费"+req.getStepConsumptionToken()+"WebX，不足30分钟按30分钟计费。");
//        }
//        if (req.getRenewalRules() != null) e.setRenewalRules(req.getRenewalRules());
//        if (req.getTimeZone() != null && !req.getTimeZone().trim().isEmpty()) e.setTimeZone(req.getTimeZone());

        // 先处理删除操作
        if (req.getDeleteUserTierOptions() != null && !req.getDeleteUserTierOptions().isEmpty()) {
            List<Map<String, Object>> existing = e.getUserTierOptions();
            if (existing != null) {
                existing.removeIf(existingItem -> {
                    Object existingValue = existingItem.get("value");
                    Object existingName = existingItem.get("name");
                    for (Map<String, Object> deleteItem : req.getDeleteUserTierOptions()) {
                        Object deleteValue = deleteItem.get("value");
                        Object deleteName = deleteItem.get("name");
                        if ((deleteValue != null && deleteValue.equals(existingValue)) ||
                                (deleteName != null && deleteName.equals(existingName))) {
                            return true; // 删除该项
                        }
                    }
                    return false;
                });
                e.setUserTierOptions(existing);
            }
        }

        if (req.getDeleteTimeOline() != null && !req.getDeleteTimeOline().isEmpty()) {
            List<Map<String, Object>> existing = e.getTimeOline();
            if (existing != null) {
                existing.removeIf(existingItem -> {
                    Object existingValue = existingItem.get("value");
                    Object existingName = existingItem.get("name");
                    for (Map<String, Object> deleteItem : req.getDeleteTimeOline()) {
                        Object deleteValue = deleteItem.get("value");
                        Object deleteName = deleteItem.get("name");
                        if ((deleteValue != null && deleteValue.equals(existingValue)) ||
                                (deleteName != null && deleteName.equals(existingName))) {
                            return true; // 删除该项
                        }
                    }
                    return false;
                });
                e.setTimeOline(existing);
            }
        }
        e.setUpdateTime(System.currentTimeMillis());
        repository.save(e);
        return true;
    }

    @Override
    public MeetingConfigV2PageResp pageQuery(MeetingConfigV2QueryReq req) {
        // 这里可以实现分页查询逻辑，暂时返回空结果
        MeetingConfigV2PageResp resp = new MeetingConfigV2PageResp();
        resp.setTotal(0L);
        resp.setPageNum(1);
        resp.setPageSize(10);
        resp.setList(new ArrayList<>());
        return resp;
    }

    private MeetingConfigV2Dto toDto(MeetingConfigV2Entity e) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new MeetingConfigV2Dto()
                .setId(e.getId())
                .setAllMic(e.getAllMic())
                .setAllMute(e.getAllMute())
                .setStepConsumptionToken(e.getStepConsumptionToken())
                .setTimeZone(e.getTimeZone())
                .setUserTierOptions(e.getUserTierOptions())
                .setTimeOline(e.getTimeOline())
                .setRenewalRules(e.getRenewalRules())
                .setIsNonPayPwd(e.getIsNonPayPwd())
                .setCreateTime(e.getCreateTime() == null ? null : sdf.format(new Date(e.getCreateTime())))
                .setUpdateTime(e.getUpdateTime() == null ? null : sdf.format(new Date(e.getUpdateTime())));
    }
}
