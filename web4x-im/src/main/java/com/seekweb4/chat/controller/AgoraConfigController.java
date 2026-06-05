package com.seekweb4.chat.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.web.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 声网(Agora) 配置读取接口
 *
 * 说明：返回前端需要的声网配置信息，不加密，直接透出。
 */
@Slf4j
@RestController
@RequestMapping("/api/agora")
public class AgoraConfigController extends BaseController {

    /**
     * 默认应用 AppId（用于前端 SDK 初始化）。
     */
    @Value("${whitelist.token.appId:}")
    private String defaultAppId;

    @Value("${filePath}")
    private String chatRoomDomain;

    @Value("${whitelist.token.appCert:}")
    private String defaultAppCert;

    @Value("${whitelist.chatRoom.orgName:}")
    private String chatOrgName;

    @Value("${whitelist.chatRoom.appName:}")
    private String chatAppName;

    @Value("${whitelist.chatRoom.clientId:}")
    private String chatClientId;

    @Value("${whitelist.chatRoom.clientSecret:}")
    private String chatClientSecret;

    /**
     * 获取前端所需的声网配置信息（不加密）。
     * GET /api/agora/config
     */
    @GetMapping("/config")
    public AjaxJson getAgoraConfig() {
        Map<String, Object> data = new HashMap<String, Object>(6);
        // 前端需要的配置信息（仅返回这6个字段）
        data.put("appId", defaultAppId);
        data.put("appCert", defaultAppCert);
        // TODO 这个lixin是需要后端自己去改的，去配置的。如果后续修改成其他的需要到这个地方去修改否则会出现错误。
        data.put("host", chatRoomDomain + "lixin");
        data.put("imAppKey", chatOrgName + "#" + chatAppName);
        data.put("imClientId", chatClientId);
        data.put("imClientSecret", chatClientSecret);

        log.info("获取声网配置：appId={}, appCert={}, host={}, imAppKey={}, imClientId={}, imClientSecret={}",
                defaultAppId, defaultAppCert, chatRoomDomain, (chatOrgName + "#" + chatAppName), chatClientId, chatClientSecret);
        return AjaxJson.success().setData(data);
    }
}