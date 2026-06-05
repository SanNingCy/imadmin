package com.seekweb4.chat.modules.live.controller;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.live.dto.LiveOrderCreateReq;
import com.seekweb4.chat.modules.live.entity.LiveBillingRule;
import com.seekweb4.chat.modules.live.entity.LiveOrderRecord;
import com.seekweb4.chat.modules.live.service.LiveBillingRuleService;
import com.seekweb4.chat.modules.live.service.LiveConfigService;
import com.seekweb4.chat.modules.live.service.LiveOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping(value = "/live", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "会议室(新)")
@Validated
public class LiveController extends BaseController {

    @Resource
    private LiveBillingRuleService billingRuleService;

    @Resource
    private LiveConfigService configService;

    @Resource
    private LiveOrderService orderService;

    @ApiOperation("获取启用的时长配置列表")
    @ApiLog("获取启用的时长配置列表")
    @GetMapping(value = "duration/enabled", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson enabledDurations() {
        return AjaxJson.success().put("list", configService.listEnabledDurations());
    }

    @ApiOperation("获取启用的人数档位列表")
    @ApiLog("获取启用的人数档位列表")
    @GetMapping(value = "tier/enabled", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson enabledTiers() {
        return AjaxJson.success().put("list", configService.listEnabledTiers());
    }

    @ApiOperation("获取当前启用的计费规则")
    @ApiLog("获取当前启用的计费规则")
    @GetMapping(value = "billingRule/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson activeBillingRule() {
        LiveBillingRule rule = billingRuleService.getActiveRule();
        return rule == null ? AjaxJson.error("未配置启用的计费规则") : AjaxJson.success().put("rule", rule);
    }

    @ApiOperation("计算金额(按当前启用计费规则)")
    @ApiLog("计算会议金额")
    @GetMapping(value = "billingRule/calcAmount", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson calcAmount(@ApiParam("会议时长(分钟)") @RequestParam Integer durationMinutes,
                               @ApiParam("人数上限(可选)") @RequestParam(required = false) Integer tierValue) {
        try {
            BigDecimal amount = billingRuleService.calcAmount(durationMinutes, tierValue);
            return AjaxJson.success().put("amount", amount);
        } catch (Exception e) {
            return AjaxJson.error(e.getMessage());
        }
    }

    @ApiOperation("创建订单(写入订单记录，状态pending_create)")
    @ApiLog("创建会议订单")
    @PostMapping(value = "order/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson createOrder(@Valid @RequestBody LiveOrderCreateReq req) {
        try {
            LiveOrderRecord record = orderService.createOrder(req);
            return AjaxJson.success("创建成功").put("order", record);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return AjaxJson.error("创建失败：" + e.getMessage());
        }
    }
}

