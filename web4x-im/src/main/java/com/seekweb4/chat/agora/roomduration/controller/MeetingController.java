package com.seekweb4.chat.agora.roomduration.controller;

import com.google.gson.Gson;
import com.seekweb4.chat.agora.delayed.DelayedMessageData;
import com.seekweb4.chat.agora.delayed.DelayedMessageService;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingCreateReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingGroupQueryReq;
import com.seekweb4.chat.agora.roomduration.entity.dto.MeetingSettingsUpdateReq;
import com.seekweb4.chat.agora.roomduration.service.IMeetingService;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping(value = "/meetings", produces = MediaType.APPLICATION_JSON_VALUE)
public class MeetingController {
    @Autowired
    private StringRedisUtils redisUtils;

    //@Resource
    @Resource(name = "roomDurationMeetingService")
    private IMeetingService meetingService;

    @Autowired
    private DelayedMessageService delayedMessageService;

    public final String mettingArea = "mettingArea:";

    // 创建会议接口：POST /meetings/create/groupId
    @PostMapping("/create/groupId")
    public AjaxJson createMeeting(
            @Validated @RequestBody MeetingCreateReq req
    ) {
        try {
            Map<String, Object> meetingData = meetingService.createMeeting(req);
            Map<String, Object> data = new HashMap<>();
            Integer meetingTime = Integer.valueOf(req.getMeetingTime());
            long endTime = (meetingTime * 60);
            req.setEndTime(endTime);
            req.setAddSize(0);

            DelayedMessageData messageData = new DelayedMessageData();
            messageData.setId(System.currentTimeMillis());
            messageData.setBusinessData(new Gson().toJson(req));
            messageData.setMessage("创建会议延时关闭任务");
            messageData.setMessageType("MeetingDelayedClose");
            //保存会议数据到 redis ，key->群ID：data:messageData
            redisUtils.setEx(mettingArea.concat(req.getOwnerId()),new Gson().toJson(messageData),2, TimeUnit.DAYS);

            delayedMessageService.sendCustomDelayedMessage(messageData,endTime, TimeUnit.SECONDS);
            log.info("发送延时消息成功:延时: {} 分钟, data:{}", meetingTime,messageData);
            data.put("createout", 200);
            data.put("meeting", meetingData);
            return AjaxJson.success().setData(data);
        } catch (IllegalArgumentException e) {
            log.error("创建会议失败", e);
            String errorMsg = e.getMessage();
            Map<String, Object> data = new HashMap<>();
            
            // 根据错误信息判断createout状态码
            if (errorMsg != null && errorMsg.contains("Token不足")) {
                data.put("createout", 1);
                return AjaxJson.error("支付失败: 您的代币不足").setData(data);
            } else if (errorMsg != null && errorMsg.contains("已有会议正在进行中")) {
                data.put("createout", 2);
                return AjaxJson.error("您已有会议正在进行中，暂时无法创建新会议，请处理当前会议").setData(data);
            } else if (errorMsg != null && (errorMsg.contains("开始时间不能早于当前时间") || errorMsg.contains("开始时间不可超过30天"))) {
                data.put("createout",4 );
                return AjaxJson.error(errorMsg).setData(data);
            } else {
                data.put("createout", 3);
                return AjaxJson.error("参数错误").setData(data);
            }
        } catch (Exception e) {
            log.error("创建会议失败", e);
            Map<String, Object> data = new HashMap<>();
            data.put("createout", 2);
            return AjaxJson.error("参数错误").setData(data);
        }
    }

    // 计算当前会议所需代币：POST /meetings/calculate/owner
    @PostMapping("/calculate/owner")
    public AjaxJson calculateOwner(@RequestBody Map<String, Object> body) {
        try {
            String ownerId = body.get("ownerId") == null ? null : String.valueOf(body.get("ownerId"));
            String meetingTime = body.get("meetingTime") == null ? null : String.valueOf(body.get("meetingTime"));
            String meetingMaxUsers = body.get("meetingMaxUsers") == null ? null : String.valueOf(body.get("meetingMaxUsers"));

            Map<String, BigDecimal> data = meetingService.calculateTokens(ownerId, meetingTime, meetingMaxUsers);
            Map<String, Object> result = new java.util.HashMap<>();
            
            // 格式化为保留两位小数的字符串
            BigDecimal availableTokens = data.get("availableTokens");
            BigDecimal meetingTokens = data.get("meetingTokens");
            BigDecimal discountMeetingTokens = data.get("discountMeetingTokens");
            
            result.put("availableTokens", availableTokens != null ? availableTokens.setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            result.put("meetingTokens", meetingTokens != null ? meetingTokens.setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            result.put("discountMeetingTokens", discountMeetingTokens != null ? discountMeetingTokens.setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            
            return AjaxJson.success().setData(result);
        } catch (IllegalArgumentException e) {
            return AjaxJson.error("参数异常");
        } catch (Exception e) {
            log.error("计算会议积分失败", e);
            return AjaxJson.error("计算失败: " + e.getMessage());
        }
    }

    // 扣费接口：POST /meetings/deduct/owner
    @PostMapping("/deduct/owner")
    public AjaxJson deductOwner(@RequestBody Map<String, Object> body) {
        try {
            String ownerId = body.get("ownerId") == null ? null : String.valueOf(body.get("ownerId"));
            String meetingTime = body.get("meetingTime") == null ? null : String.valueOf(body.get("meetingTime"));
            String meetingMaxUsers = body.get("meetingMaxUsers") == null ? null : String.valueOf(body.get("meetingMaxUsers"));
            String title = body.get("title") == null ? null : String.valueOf(body.get("title"));
            String info = body.get("info") == null ? null : String.valueOf(body.get("info"));

            Map<String, BigDecimal> data = meetingService.deductTokens(ownerId, meetingTime, meetingMaxUsers, title, info);
            Map<String, Object> result = new HashMap<>();
            result.put("deductTokens", data.get("deductTokens"));
            result.put("remainTokens", data.get("remainTokens"));
            return AjaxJson.success().setData(result);
        } catch (IllegalArgumentException e) {
            return AjaxJson.error(e.getMessage());
        } catch (Exception e) {
            log.error("扣费失败", e);
            return AjaxJson.error("扣费失败: " + e.getMessage());
        }
    }
    
    // 查询群当前会议信息：POST /meetings/get/groupById
    @PostMapping("/get/groupById")
    public AjaxJson getGroupCurrent(@RequestBody MeetingGroupQueryReq mgq) {
        try {
            Map<String, Object> data = meetingService.getGroupCurrentMeeting(mgq.getGroupId());
            return AjaxJson.success().setData(data);
        } catch (Exception e) {
            log.error("查询群当前会议信息失败 groupId={}", mgq.getGroupId(), e);
            return AjaxJson.error("查询失败: " + e.getMessage());
        }
    }

    // 添加会议时长：POST /meetings/add/group/time
    @PostMapping("/add/group/time")
    public AjaxJson addGroupTime(@RequestBody Map<String, Object> body) {
        try {
            String groupId = body.get("groupId") == null ? null : String.valueOf(body.get("groupId"));
            String ownerId = body.get("ownerId") == null ? null : String.valueOf(body.get("ownerId"));
            //分钟数
            String meetingTime = body.get("meetingTime") == null ? null : String.valueOf(body.get("meetingTime"));
            String roomId = body.get("roomId") == null ? null : String.valueOf(body.get("roomId"));
            
            // 验证必传参数：ownerId和meetingTime
            if (ownerId == null || meetingTime == null) {
                return AjaxJson.error("参数异常：ownerId和meetingTime为必传参数");
            }

            boolean ok = meetingService.addGroupTime(groupId, ownerId, meetingTime, roomId);
            if (ok) {
                //获取会议数据
                String mettingData = redisUtils.get(mettingArea.concat(ownerId));
                DelayedMessageData  delayedMessageData = new Gson().fromJson(mettingData, DelayedMessageData.class);
                MeetingCreateReq meetingCreateReq = new Gson().fromJson(delayedMessageData.getBusinessData(), MeetingCreateReq.class);
                //延迟队列延迟时间关闭；
                delayedMessageService.cancelDelayedMessage(delayedMessageData);
                //获取前一次结束时间；
                Long lastEndTime = meetingCreateReq.getEndTime();
                //基于前一次结束时间增加延长的meetingTime的时间，meetingTime是分钟数；
                Integer addTime = Integer.valueOf(meetingTime);
                meetingCreateReq.setMeetingTime(meetingCreateReq.getMeetingTime() + addTime);
                long currnetEndTime = lastEndTime + (addTime * 60);
                meetingCreateReq.setEndTime(currnetEndTime);
                meetingCreateReq.setAddSize(meetingCreateReq.getAddSize()+1);

                DelayedMessageData messageData = new DelayedMessageData();
                messageData.setId(System.currentTimeMillis());
                messageData.setBusinessData(new Gson().toJson(meetingCreateReq));
                messageData.setMessage("创建会议延时关闭任务");
                messageData.setMessageType("MeetingDelayedClose");
                //保存新的会议数据到 redis ，key->mettingArea:群ID：data:messageData
                redisUtils.setEx(mettingArea.concat(meetingCreateReq.getOwnerId()),new Gson().toJson(messageData),2, TimeUnit.DAYS);
                delayedMessageService.sendCustomDelayedMessage(messageData,currnetEndTime, TimeUnit.SECONDS);
                return AjaxJson.success("加时成功");
            }
            return AjaxJson.error("参数异常");
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Token不足")) {
                Map<String, Object> data = new HashMap<>();
                data.put("createout", 1);
                return AjaxJson.error("支付失败: 您的代币不足").setData(data);
            }
            if (msg != null && msg.contains("免密支付")) {
                return AjaxJson.error("暂未开启免密支付不支持该操作！");
            }
            return AjaxJson.error(msg != null ? msg : "参数异常");
        } catch (Exception e) {
            log.error("添加会议时长失败", e);
            return AjaxJson.error("添加失败: " + e.getMessage());
        }
    }

    // 查询群内正在进行的会议：POST /meetings/group/active
    @PostMapping("/group/active")
    public AjaxJson getGroupActiveMeetings(@RequestBody Map<String, Object> body) {
        try {
            String groupId = body.get("groupId") == null ? null : String.valueOf(body.get("groupId"));
            if (groupId == null || groupId.trim().isEmpty()) {
                return AjaxJson.error("群ID不能为空");
            }
            Map<String, Object> result = meetingService.getGroupActiveMeetings(groupId);
            return AjaxJson.success().setData(result);
        } catch (Exception e) {
            log.error("查询群内正在进行的会议失败 groupId={}", body.get("groupId"), e);
            return AjaxJson.error("查询失败: " + e.getMessage());
        }
    }

    // 查询群内活跃的会议（基于status字段）：POST /meetings/group/active/status
    @PostMapping("/group/active/status")
    public AjaxJson getGroupActiveMeetingsByStatus(@RequestBody Map<String, Object> body) {
        try {
            String groupId = body.get("groupId") == null ? null : String.valueOf(body.get("groupId"));
            if (groupId == null || groupId.trim().isEmpty()) {
                return AjaxJson.error("群ID不能为空");
            }
            Map<String, Object> result = meetingService.getGroupActiveMeetingsByStatus(groupId);
            return AjaxJson.success().setData(result);
        } catch (Exception e) {
            log.error("查询群内活跃会议失败 groupId={}", body.get("groupId"), e);
            return AjaxJson.error("查询失败: " + e.getMessage());
        }
    }

    // 修改会议室设置：POST /meetings/settings/update
    @PostMapping("/settings/update")
    public AjaxJson updateMeetingSettings(@RequestBody Map<String, Object> body) {
        try {
            String roomId = body.get("roomId") == null ? null : String.valueOf(body.get("roomId"));
            Boolean allMic = body.get("allMic") == null ? null : Boolean.valueOf(String.valueOf(body.get("allMic")));
            Boolean allMute = body.get("allMute") == null ? null : Boolean.valueOf(String.valueOf(body.get("allMute")));
            String status = body.get("status") == null ? null : String.valueOf(body.get("status"));
            
            MeetingSettingsUpdateReq req =
                new MeetingSettingsUpdateReq()
                    .setRoomId(roomId)
                    .setAllMic(allMic)
                    .setAllMute(allMute)
                    .setStatus(status);
            
            boolean success = meetingService.updateMeetingSettings(req);
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            return AjaxJson.success().setData(result);
        } catch (IllegalArgumentException e) {
            return AjaxJson.error(e.getMessage());
        } catch (Exception e) {
            log.error("修改会议室设置失败 roomId={}", body.get("roomId"), e);
            return AjaxJson.error("修改失败: " + e.getMessage());
        }
    }

    // 根据会议室ID查询会议信息：GET /meetings/settings/detail/{roomId}
    @GetMapping("/settings/detail/{roomId}")
    public AjaxJson getMeetingDetail(@PathVariable("roomId") String roomId) {
        try {
            Map<String, Object> data = meetingService.getMeetingByRoomId(roomId);
            return AjaxJson.success().setData(data);
        } catch (IllegalArgumentException e) {
            return AjaxJson.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询会议室详情失败 roomId={}", roomId, e);
            return AjaxJson.error("查询失败: " + e.getMessage());
        }
    }
}


