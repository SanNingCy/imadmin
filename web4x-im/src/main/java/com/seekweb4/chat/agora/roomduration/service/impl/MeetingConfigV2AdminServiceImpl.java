package com.seekweb4.chat.agora.roomduration.service.impl;

import com.seekweb4.chat.agora.roomadmin.bean.RoomDestroyReq;
import com.seekweb4.chat.agora.roomduration.entity.MeetingConfigV2Entity;
import com.seekweb4.chat.agora.roomduration.entity.MeetingEntity;
import com.seekweb4.chat.agora.roomduration.entity.dto.*;
import com.seekweb4.chat.agora.roomduration.repository.MeetingConfigV2Repository;
import com.seekweb4.chat.agora.roomduration.repository.MeetingRepository;
import com.seekweb4.chat.agora.roomduration.service.IMeetingConfigV2AdminService;
import com.seekweb4.chat.agora.service.IRoomV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MeetingConfigV2AdminServiceImpl implements IMeetingConfigV2AdminService {

    @Resource
    private MeetingConfigV2Repository repository;

    @Resource
    private MeetingServiceImpl meetingService;

    @Resource
    private IRoomV2Service roomV2Service;

    @Resource
    private MeetingRepository meetingRepository;

    @Value("${whitelist.token.appId:}")
    private String defaultAppId;

    // 若无法查询到sceneId，兜底使用直播场景
    private static final String DEFAULT_SCENE_ID = "live_streaming";

    @Override
    public MeetingConfigV2PageResp pageQuery(MeetingConfigV2QueryReq req) {
        List<MeetingConfigV2Entity> all = repository.findAll();
        int pageNum = req.getPageNum() == null ? 1 : req.getPageNum();
        int pageSize = req.getPageSize() == null ? 10 : req.getPageSize();
        int from = Math.max(0, (pageNum - 1) * pageSize);
        int to = Math.min(all.size(), from + pageSize);
        List<MeetingConfigV2Dto> list = all.subList(from, to).stream().map(this::toDto).collect(Collectors.toList());
        return new MeetingConfigV2PageResp()
                .setCount(all.size())
//                .setTotal(all.size())
//                .setPageNum(pageNum)
//                .setPageSize(pageSize)
                .setList(list);
    }

    @Override
    public MeetingConfigV2Dto getById(String id) {
        return repository.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public String create(MeetingConfigV2CreateReq req) {
        MeetingConfigV2Entity e = new MeetingConfigV2Entity();
        e.setId(UUID.randomUUID().toString().replace("-", ""));
        e.setAllMic(req.getAllMic());
        e.setAllMute(req.getAllMute());
        e.setStepConsumptionToken(req.getStepConsumptionToken());
        e.setTimeZone(req.getTimeZone());
        e.setUserTierOptions(req.getUserTierOptions());
        e.setTimeOline(req.getTimeOline());
        // 初始化默认规则与免密支付开关（若需要）
        if (e.getStepConsumptionToken() != null) {
            e.setRenewalRules("续费规则：每30分钟收费"+e.getStepConsumptionToken()+"WebX，不足30分钟按30分钟计费。");
        }
        long now = System.currentTimeMillis();
        e.setCreateTime(now);
        e.setUpdateTime(now);
        repository.save(e);
        return e.getId();
    }

    @Override
    public boolean update(MeetingConfigV2UpdateReq req) {
        if (req.getId() == null) return false;
        MeetingConfigV2Entity e = repository.findById(req.getId()).orElse(null);
        if (e == null) return false;
        if (req.getAllMic() != null) e.setAllMic(req.getAllMic());
        if (req.getAllMute() != null) e.setAllMute(req.getAllMute());
        if (req.getStepConsumptionToken() != null) {
            e.setStepConsumptionToken(req.getStepConsumptionToken());
            e.setRenewalRules("续费规则：每30分钟收费"+req.getStepConsumptionToken()+"WebX，不足30分钟按30分钟计费。");
        }
        if (req.getRenewalRules() != null) e.setRenewalRules(req.getRenewalRules());
        if (req.getTimeZone() != null && !req.getTimeZone().trim().isEmpty()) e.setTimeZone(req.getTimeZone());
        if (req.getIsNonPayPwd() != null) {
            e.setIsNonPayPwd(req.getIsNonPayPwd());
        }

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
        
        // 合并更新 userTierOptions
        if (req.getUserTierOptions() != null && !req.getUserTierOptions().isEmpty()) {
            List<Map<String, Object>> existing = e.getUserTierOptions();
            if (existing == null) {
                e.setUserTierOptions(req.getUserTierOptions());
            } else {
                // 合并逻辑：支持更新现有项和添加新项
                for (Map<String, Object> newItem : req.getUserTierOptions()) {
                    Object newValue = newItem.get("value");
                    Object newName = newItem.get("name");
                    if (newValue != null && newName != null) {
                        // 查找是否存在相同value的项（用于更新）
                        boolean found = false;
                        for (Map<String, Object> existingItem : existing) {
                            if (newValue.equals(existingItem.get("value"))) {
                                // 更新现有项（包括value和name）
                                existingItem.putAll(newItem);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            // 添加新项
                            throw new IllegalArgumentException("用户等级未找到");
//                            existing.add(newItem);
                        }
                    }
                }
                e.setUserTierOptions(existing);
            }
        }
        
        // 合并更新 timeOline
        if (req.getTimeOline() != null && !req.getTimeOline().isEmpty()) {
            List<Map<String, Object>> existing = e.getTimeOline();
            if (existing == null) {
                e.setTimeOline(req.getTimeOline());
            } else {
                // 合并逻辑：支持更新现有项和添加新项
                for (Map<String, Object> newItem : req.getTimeOline()) {
                    Object newValue = newItem.get("value");
                    Object newName = newItem.get("name");
                    if (newValue != null && newName != null) {
                        // 查找是否存在相同value的项（用于更新）
                        boolean found = false;
                        for (Map<String, Object> existingItem : existing) {
                            if (newValue.equals(existingItem.get("value"))) {
                                // 更新现有项（包括value和name）
                                existingItem.putAll(newItem);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            // 添加新项
                            throw new IllegalArgumentException("会议时长未找到");
//                            existing.add(newItem);
                        }
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
    public boolean deleteById(String id) {
        if (id == null) return false;
        repository.deleteById(id);
        return true;
    }

    @Override
    public Map<String, Object> destroyRoom(RoomDestroyReq request) throws Exception {
        log.info("开始解散会议，roomId：{}，操作者：{}", request.getRoomId(), request.getOperatorId());

        String roomId = request.getRoomId();
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("房间ID不能为空");
        }

        // 1) 优先通过声网查询房间详情，获取 appId/sceneId；查不到则使用默认兜底
        // TODO 这两个暂时使用默认的配置，到时候创建后使用真实的
        String appId = defaultAppId;
        String sceneId = DEFAULT_SCENE_ID;

        if (appId == null || appId.trim().isEmpty() || sceneId == null || sceneId.trim().isEmpty()) {
            throw new RuntimeException("会议室不存在");
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

        // 3) 更新本地 MeetingEntity 状态（若存在）
        try {
            java.util.Optional<MeetingEntity> meetingOpt = meetingRepository.findByRoomId(roomId);
            if (meetingOpt.isPresent()) {
                MeetingEntity entity = meetingOpt.get();
                if (!"destroyed".equals(entity.getStatus())) {
                    entity.setStatus("destroyed");
                    entity.setUpdateTime(System.currentTimeMillis());
                    if (entity.getEndTime() == null) {
                        entity.setEndTime(System.currentTimeMillis());
                    }
                    meetingRepository.save(entity);
                }
            }
        } catch (Exception e) {
            log.warn("更新本地MeetingEntity状态失败 roomId={}", roomId, e);
        }

        // 4) 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("roomId", roomId);
        result.put("destroyTime", System.currentTimeMillis());
        result.put("operatorId", request.getOperatorId());
        result.put("reason", request.getReason());
        log.info("解散会议完成，roomId：{}", roomId);
        return result;
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


