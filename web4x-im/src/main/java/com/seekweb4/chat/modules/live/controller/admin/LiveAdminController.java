package com.seekweb4.chat.modules.live.controller.admin;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.live.dto.*;
import com.seekweb4.chat.modules.live.entity.LiveBillingRule;
import com.seekweb4.chat.modules.live.entity.LiveFixedPriceConfig;
import com.seekweb4.chat.modules.live.entity.LiveOrderRecord;
import com.seekweb4.chat.modules.live.entity.LiveTimeDurationConfig;
import com.seekweb4.chat.modules.live.entity.LiveUserTierConfig;
import com.seekweb4.chat.modules.live.service.LiveBillingRuleService;
import com.seekweb4.chat.modules.live.service.LiveConfigService;
import com.seekweb4.chat.modules.live.service.LiveFixedPriceConfigService;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/admin/live", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "会议室(新)后台管理")
@Validated
public class LiveAdminController extends BaseController {

    @Resource
    private LiveBillingRuleService billingRuleService;

    @Resource
    private LiveConfigService configService;

    @Resource
    private LiveOrderService orderService;

    @Resource
    private LiveFixedPriceConfigService fixedPriceConfigService;

    // ===== 计费规则 =====

    @ApiOperation("分页查询计费规则")
    @ApiLog("分页查询计费规则")
    @GetMapping(value = "billingRule/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson billingRulePage(LiveBillingRuleQueryDto queryDto) {
        try {
            Page<LiveBillingRule> page = billingRuleService.page(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询计费规则失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("分页查询会议室配置(计费规则+人数/时长选项)")
    @ApiLog("分页查询会议室配置")
    @GetMapping(value = "roomConfig/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson roomConfigPage(LiveRoomConfigQueryDto queryDto) {
        try {
            // 规则分页来自 t_live_billing_rule
            LiveBillingRuleQueryDto ruleQuery = new LiveBillingRuleQueryDto();
            ruleQuery.setPageNo(queryDto.getPageNo());
            ruleQuery.setPageSize(queryDto.getPageSize());
            ruleQuery.setOrderBy(queryDto.getOrderBy());
            ruleQuery.setId(queryDto.getId());
            ruleQuery.setStatus(queryDto.getStatus());
            ruleQuery.setUnitPrice(queryDto.getUnitPrice());
            ruleQuery.setCreateBy(queryDto.getCreateBy());
            ruleQuery.setUpdateBy(queryDto.getUpdateBy());
            Page<LiveBillingRule> rulePage = billingRuleService.page(ruleQuery);

            // 人数/时长选项来自配置表（按启用状态）
            List<LiveRoomConfigOptionVo> tierOptions = configService.listEnabledTiers().stream()
                    .map(t -> new LiveRoomConfigOptionVo(t.getTierName(), t.getTierValue()))
                    .collect(Collectors.toList());
            List<LiveRoomConfigOptionVo> durationOptions = configService.listEnabledDurations().stream()
                    .map(d -> new LiveRoomConfigOptionVo(d.getDurationName(), d.getDurationValue()))
                    .collect(Collectors.toList());

            List<LiveRoomConfigVo> voList = rulePage.getList().stream().map(rule -> {
                LiveRoomConfigVo vo = new LiveRoomConfigVo();
                vo.setId(rule.getId() == null ? null : String.valueOf(rule.getId()));
                vo.setStepConsumptionToken(rule.getUnitPrice());
                vo.setUserTierOptions(tierOptions);
                vo.setTimeOline(durationOptions);
                vo.setRenewalRules(rule.getRoundingRule());
                vo.setCreateTime(rule.getCreateTime());
                vo.setUpdateTime(rule.getUpdateTime());
                return vo;
            }).collect(Collectors.toList());

            Page<LiveRoomConfigVo> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
            page.setCount(rulePage.getCount());
            page.setList(voList);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询会议室配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("计费规则详情")
    @ApiLog("查询计费规则详情")
    @GetMapping(value = "billingRule/queryById", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson billingRuleQueryById(@RequestParam Long id) {
        LiveBillingRule rule = billingRuleService.getById(id);
        return rule == null ? AjaxJson.error("不存在") : AjaxJson.success().put("rule", rule);
    }

    @ApiOperation("计费规则详情(兼容REST路径)")
    @GetMapping(value = "billingRule/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson billingRuleDetail(@PathVariable Long id) {
        LiveBillingRule rule = billingRuleService.getById(id);
        return rule == null ? AjaxJson.error("不存在") : AjaxJson.success().put("rule", rule);
    }

    @ApiOperation("新增计费规则")
    @ApiLog("新增计费规则")
    @PostMapping(value = "billingRule/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson billingRuleSave(@RequestBody LiveBillingRule rule) {
        try {
            boolean ok = billingRuleService.create(rule);
            return ok ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增计费规则失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增计费规则(兼容create)")
    @PostMapping(value = "billingRule/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson billingRuleCreate(@RequestBody LiveBillingRule rule) {
        try {
            boolean ok = billingRuleService.create(rule);
            return ok ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增计费规则失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新计费规则")
    @ApiLog("更新计费规则")
    @PostMapping(value = "billingRule/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson billingRuleUpdate(@RequestBody LiveBillingRule rule) {
        try {
            boolean ok = billingRuleService.update(rule);
            return ok ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新计费规则失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除计费规则(软删)")
    @ApiLog("删除计费规则")
    @DeleteMapping(value = "billingRule/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson billingRuleDelete(@RequestParam Long id, @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = billingRuleService.delete(id, updateBy);
            return ok ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除计费规则失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    @ApiOperation("计算订单金额(按当前启用计费规则)")
    @ApiLog("计算订单金额")
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

    // ===== 时长配置 =====

    @ApiOperation("分页查询会议时长配置")
    @ApiLog("分页查询会议时长配置")
    @GetMapping(value = "duration/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson durationPage(LiveTimeDurationConfigQueryDto queryDto) {
        try {
            Page<LiveTimeDurationConfig> page = configService.pageDuration(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询时长配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("时长配置详情")
    @ApiLog("查询时长配置详情")
    @GetMapping(value = "duration/queryById", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson durationQueryById(@RequestParam Long id) {
        LiveTimeDurationConfig config = configService.getDurationById(id);
        return config == null ? AjaxJson.error("不存在") : AjaxJson.success().put("config", config);
    }

    @ApiOperation("时长配置详情(兼容REST路径)")
    @GetMapping(value = "duration/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson durationDetail(@PathVariable Long id) {
        LiveTimeDurationConfig config = configService.getDurationById(id);
        return config == null ? AjaxJson.error("不存在") : AjaxJson.success().put("config", config);
    }

    @ApiOperation("新增时长配置")
    @ApiLog("新增时长配置")
    @PostMapping(value = "duration/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson durationSave(@RequestBody LiveTimeDurationConfig config) {
        try {
            boolean ok = configService.createDuration(config);
            return ok ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增时长配置失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增时长配置(兼容create)")
    @PostMapping(value = "duration/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson durationCreate(@RequestBody LiveTimeDurationConfig config) {
        try {
            boolean ok = configService.createDuration(config);
            return ok ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增时长配置失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新时长配置")
    @ApiLog("更新时长配置")
    @PostMapping(value = "duration/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson durationUpdate(@RequestBody LiveTimeDurationConfig config) {
        try {
            boolean ok = configService.updateDuration(config);
            return ok ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新时长配置失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除时长配置(软删)")
    @ApiLog("删除时长配置")
    @DeleteMapping(value = "duration/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson durationDelete(@RequestParam Long id, @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = configService.deleteDuration(id, updateBy);
            return ok ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除时长配置失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    @ApiOperation("启用的时长配置列表")
    @GetMapping(value = "duration/enabled", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson durationEnabled() {
        return AjaxJson.success().put("list", configService.listEnabledDurations());
    }

    // ===== 固定价格配置 =====

    @ApiOperation("固定价格-时长下拉(id/名称/分钟值，未删除含禁用)")
    @GetMapping(value = "fixedPrice/options/duration", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson fixedPriceDurationOptions() {
        return AjaxJson.success().put("list", configService.listDurationSelectOptions());
    }

    @ApiOperation("固定价格-人数档位下拉(id/名称/人数上限，未删除含禁用)")
    @GetMapping(value = "fixedPrice/options/tier", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson fixedPriceTierOptions() {
        return AjaxJson.success().put("list", configService.listTierSelectOptions());
    }

    @ApiOperation("分页查询固定价格配置(含时长/人数名称与值)")
    @ApiLog("分页查询固定价格配置")
    @GetMapping(value = "fixedPrice/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson fixedPricePage(LiveFixedPriceConfigQueryDto queryDto) {
        try {
            return AjaxJson.success().put("page", fixedPriceConfigService.page(queryDto));
        } catch (Exception e) {
            log.error("分页查询固定价格配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("固定价格配置详情")
    @ApiLog("查询固定价格配置详情")
    @GetMapping(value = "fixedPrice/queryById", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson fixedPriceQueryById(@RequestParam Long id) {
        LiveFixedPriceConfigVo vo = fixedPriceConfigService.getById(id);
        return vo == null ? AjaxJson.error("不存在") : AjaxJson.success().put("config", vo);
    }

    @ApiOperation("固定价格配置详情(兼容REST路径)")
    @GetMapping(value = "fixedPrice/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson fixedPriceDetail(@PathVariable Long id) {
        LiveFixedPriceConfigVo vo = fixedPriceConfigService.getById(id);
        return vo == null ? AjaxJson.error("不存在") : AjaxJson.success().put("config", vo);
    }

    @ApiOperation("新增固定价格配置")
    @ApiLog("新增固定价格配置")
    @PostMapping(value = "fixedPrice/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson fixedPriceSave(@RequestBody LiveFixedPriceConfig config) {
        try {
            boolean ok = fixedPriceConfigService.create(config);
            return ok ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增固定价格配置失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增固定价格配置(兼容create)")
    @PostMapping(value = "fixedPrice/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson fixedPriceCreate(@RequestBody LiveFixedPriceConfig config) {
        try {
            boolean ok = fixedPriceConfigService.create(config);
            return ok ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增固定价格配置失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新固定价格配置")
    @ApiLog("更新固定价格配置")
    @PostMapping(value = "fixedPrice/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson fixedPriceUpdate(@RequestBody LiveFixedPriceConfig config) {
        try {
            boolean ok = fixedPriceConfigService.update(config);
            return ok ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新固定价格配置失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除固定价格配置(软删)")
    @ApiLog("删除固定价格配置")
    @DeleteMapping(value = "fixedPrice/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson fixedPriceDelete(@RequestParam Long id, @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = fixedPriceConfigService.delete(id, updateBy);
            return ok ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除固定价格配置失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    // ===== 人数档位 =====

    @ApiOperation("分页查询人数档位配置")
    @ApiLog("分页查询人数档位配置")
    @GetMapping(value = "tier/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson tierPage(LiveUserTierConfigQueryDto queryDto) {
        try {
            Page<LiveUserTierConfig> page = configService.pageTier(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询人数档位失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("人数档位详情")
    @ApiLog("查询人数档位详情")
    @GetMapping(value = "tier/queryById", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson tierQueryById(@RequestParam Long id) {
        LiveUserTierConfig config = configService.getTierById(id);
        return config == null ? AjaxJson.error("不存在") : AjaxJson.success().put("config", config);
    }

    @ApiOperation("人数档位详情(兼容REST路径)")
    @GetMapping(value = "tier/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson tierDetail(@PathVariable Long id) {
        LiveUserTierConfig config = configService.getTierById(id);
        return config == null ? AjaxJson.error("不存在") : AjaxJson.success().put("config", config);
    }

    @ApiOperation("新增人数档位")
    @ApiLog("新增人数档位")
    @PostMapping(value = "tier/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson tierSave(@RequestBody LiveUserTierConfig config) {
        try {
            boolean ok = configService.createTier(config);
            return ok ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增人数档位失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增人数档位(兼容create)")
    @PostMapping(value = "tier/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson tierCreate(@RequestBody LiveUserTierConfig config) {
        try {
            boolean ok = configService.createTier(config);
            return ok ? AjaxJson.success("新增成功") : AjaxJson.error("新增失败");
        } catch (Exception e) {
            log.error("新增人数档位失败", e);
            return AjaxJson.error("新增失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新人数档位")
    @ApiLog("更新人数档位")
    @PostMapping(value = "tier/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson tierUpdate(@RequestBody LiveUserTierConfig config) {
        try {
            boolean ok = configService.updateTier(config);
            return ok ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新人数档位失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除人数档位(软删)")
    @ApiLog("删除人数档位")
    @DeleteMapping(value = "tier/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson tierDelete(@RequestParam Long id, @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = configService.deleteTier(id, updateBy);
            return ok ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除人数档位失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }

    @ApiOperation("启用的人数档位列表")
    @GetMapping(value = "tier/enabled", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson tierEnabled() {
        return AjaxJson.success().put("list", configService.listEnabledTiers());
    }

    // ===== 订单 =====

    @ApiOperation("分页查询会议订单")
    @ApiLog("分页查询会议订单")
    @GetMapping(value = "order/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson orderPage(LiveOrderRecordQueryDto queryDto) {
        try {
            Page<LiveOrderRecord> page = orderService.page(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询订单失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("订单详情")
    @ApiLog("查询订单详情")
    @GetMapping(value = "order/queryById", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson orderQueryById(@RequestParam Long id) {
        LiveOrderRecord record = orderService.getById(id);
        return record == null ? AjaxJson.error("不存在") : AjaxJson.success().put("order", record);
    }

    @ApiOperation("订单详情(兼容REST路径)")
    @GetMapping(value = "order/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson orderDetail(@PathVariable Long id) {
        LiveOrderRecord record = orderService.getById(id);
        return record == null ? AjaxJson.error("不存在") : AjaxJson.success().put("order", record);
    }

    @ApiOperation("按订单号查询订单")
    @ApiLog("按订单号查询订单")
    @GetMapping(value = "order/queryByOrderNo", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson orderByOrderNo(@RequestParam String orderNo) {
        LiveOrderRecord record = orderService.getByOrderNo(orderNo);
        return record == null ? AjaxJson.error("不存在") : AjaxJson.success().put("order", record);
    }

    @ApiOperation("创建会议订单(写入订单记录，状态pending_create)")
    @ApiLog("创建会议订单")
    @PostMapping(value = "order/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson orderSave(@Valid @RequestBody LiveOrderCreateReq req) {
        try {
            LiveOrderRecord record = orderService.createOrder(req);
            return AjaxJson.success("创建成功").put("order", record);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return AjaxJson.error("创建失败：" + e.getMessage());
        }
    }

    @ApiOperation("创建会议订单(兼容create)")
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

    @ApiOperation("更新订单状态")
    @ApiLog("更新订单状态")
    @PostMapping(value = "order/updateStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson updateOrderStatus(@RequestParam Long id,
                                     @RequestParam String liveStatus,
                                     @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = orderService.updateStatus(id, liveStatus, updateBy);
            return ok ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除订单(软删)")
    @ApiLog("删除订单")
    @DeleteMapping(value = "order/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson deleteOrder(@RequestParam Long id, @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = orderService.delete(id, updateBy);
            return ok ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除订单失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }
}

