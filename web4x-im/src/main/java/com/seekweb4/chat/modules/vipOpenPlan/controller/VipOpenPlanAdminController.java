package com.seekweb4.chat.modules.vipOpenPlan.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.vipOpenPlan.dto.VipOpenPlanQueryDto;
import com.seekweb4.chat.modules.vipOpenPlan.entity.VipOpenPlan;
import com.seekweb4.chat.modules.vipOpenPlan.service.VipOpenPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@Slf4j
@RestController
@RequestMapping(value = "/admin/ops/vipOpenPlan", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "平台运营-会员开通套餐")
public class VipOpenPlanAdminController extends BaseController {

    @Resource
    private VipOpenPlanService vipOpenPlanService;

    @ApiOperation("分页查询会员开通套餐")
    @GetMapping("/page")
    public AjaxJson page(VipOpenPlanQueryDto queryDto) {
        try {
            Page<VipOpenPlan> page = vipOpenPlanService.page(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询会员开通套餐失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询会员开通套餐")
    @GetMapping("/queryById/{id}")
    public AjaxJson getById(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            VipOpenPlan plan = vipOpenPlanService.getById(id);
            if (plan == null) {
                return AjaxJson.error("套餐不存在");
            }
            return AjaxJson.success().put("plan", plan);
        } catch (Exception e) {
            log.error("查询会员开通套餐失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增会员开通套餐")
    @PostMapping("/save")
    public AjaxJson save(@RequestBody VipOpenPlan plan) {
        try {
            if (plan.getPlanName() == null || plan.getPlanName().trim().isEmpty()) {
                return AjaxJson.error("套餐名称不能为空");
            }
            if (plan.getDurationDays() == null || plan.getDurationDays() < 1) {
                return AjaxJson.error("会员天数须大于0");
            }
            if (plan.getPrice() == null) {
                return AjaxJson.error("价格不能为空");
            }
            boolean ok = vipOpenPlanService.save(plan);
            return ok ? AjaxJson.success("保存成功") : AjaxJson.error("保存失败");
        } catch (Exception e) {
            log.error("新增会员开通套餐失败", e);
            return AjaxJson.error("保存失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新会员开通套餐")
    @PutMapping("/update")
    public AjaxJson update(@RequestBody VipOpenPlan plan) {
        try {
            if (plan.getId() == null) {
                return AjaxJson.error("id不能为空");
            }
            boolean ok = vipOpenPlanService.update(plan);
            return ok ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新会员开通套餐失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("启用/停用会员开通套餐")
    @PutMapping("/updateStatus")
    public AjaxJson updateStatus(@ApiParam("主键ID") @RequestParam Long id,
                                 @ApiParam("状态 1:启用 0:停用") @RequestParam Integer status,
                                 @ApiParam("更新人") @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = vipOpenPlanService.updateStatus(id, status, updateBy);
            return ok ? AjaxJson.success("操作成功") : AjaxJson.error("操作失败");
        } catch (Exception e) {
            log.error("更新会员开通套餐状态失败", e);
            return AjaxJson.error("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除会员开通套餐（逻辑删除）")
    @DeleteMapping("/remove")
    public AjaxJson remove(@ApiParam("主键ID") @RequestParam Long id,
                           @ApiParam("更新人") @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = vipOpenPlanService.remove(id, updateBy);
            return ok ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除会员开通套餐失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }
}
