package com.seekweb4.chat.agora.roomduration.controller;

import com.seekweb4.chat.agora.roomduration.entity.MeetingConfigV2Entity;
import com.seekweb4.chat.agora.roomduration.service.IMeetingConfigV2Service;
import com.seekweb4.chat.common.json.AjaxJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 会议配置V2接口（读取Mongo中的配置，首次自动初始化）
 */
@Slf4j
@RestController
@RequestMapping(value = "/meetings", produces = MediaType.APPLICATION_JSON_VALUE)
public class MeetingConfigV2Controller {

    @Resource
    private IMeetingConfigV2Service meetingConfigV2Service;

    /**
     * GET: /meetings/get/config/v2
     */
    @GetMapping("/get/config/v2")
    @ResponseBody
    public AjaxJson getMeetingConfigV2() {
        try {
            MeetingConfigV2Entity cfg = meetingConfigV2Service.getOrInit();
            long nowMillis = System.currentTimeMillis();
            Map<String, Object> resp = new HashMap<>();
            // 将config字段的内容直接放到data下
//            resp.put("config", cfg);
            resp.put("id", cfg.getId());
            resp.put("allMic", cfg.getAllMic());
            resp.put("allMute", cfg.getAllMute());
            resp.put("stepConsumptionToken", cfg.getStepConsumptionToken());
            resp.put("timeZone", cfg.getTimeZone());
            resp.put("userTierOptions", cfg.getUserTierOptions());
            resp.put("timeOline", cfg.getTimeOline());
            resp.put("createTime", cfg.getCreateTime());
            resp.put("updateTime", cfg.getUpdateTime());
            resp.put("now", nowMillis);
            resp.put("renewalRules", cfg.getRenewalRules());
            resp.put("isNonPayPwd",cfg.getIsNonPayPwd());
            return AjaxJson.success().setData(resp);
        } catch (Exception e) {
            log.error("获取会议配置V2失败", e);
            return AjaxJson.error("获取配置失败: " + e.getMessage());
        }
    }
}


