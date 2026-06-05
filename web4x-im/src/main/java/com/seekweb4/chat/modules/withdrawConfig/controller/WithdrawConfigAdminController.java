package com.seekweb4.chat.modules.withdrawConfig.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.withdrawConfig.dto.WithdrawConfigQueryDto;
import com.seekweb4.chat.modules.withdrawConfig.entity.WithdrawConfig;
import com.seekweb4.chat.modules.withdrawConfig.service.WithdrawConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@Slf4j
@RestController
@RequestMapping(value = "/admin/asset/withdrawConfig", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "资产后台管理-提现金额配置")
public class WithdrawConfigAdminController extends BaseController {

    @Resource
    private WithdrawConfigService withdrawConfigService;

    @ApiOperation("分页查询提现金额配置")
    @GetMapping("/page")
    public AjaxJson page(WithdrawConfigQueryDto queryDto) {
        try {
            Page<WithdrawConfig> page = withdrawConfigService.page(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询提现金额配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询提现金额配置")
    @GetMapping("/queryById/{id}")
    public AjaxJson getById(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            WithdrawConfig config = withdrawConfigService.getById(id);
            if (config == null) {
                return AjaxJson.error("提现配置不存在");
            }
            return AjaxJson.success().put("config", config);
        } catch (Exception e) {
            log.error("查询提现金额配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增提现金额配置")
    @PostMapping("/save")
    public AjaxJson save(@RequestBody WithdrawConfig config) {
        try {
            boolean ok = withdrawConfigService.save(config);
            if (ok) {
                return AjaxJson.success("保存成功");
            }
            return AjaxJson.error("保存失败");
        } catch (Exception e) {
            log.error("新增提现金额配置失败", e);
            return AjaxJson.error("保存失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新提现金额配置")
    @PutMapping("/update")
    public AjaxJson update(@RequestBody WithdrawConfig config) {
        try {
            boolean ok = withdrawConfigService.update(config);
            if (ok) {
                return AjaxJson.success("更新成功");
            }
            return AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新提现金额配置失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除提现金额配置")
    @DeleteMapping("/remove")
    public AjaxJson remove(@ApiParam("主键ID") @RequestParam Long id) {
        try {
            boolean ok = withdrawConfigService.remove(id);
            if (ok) {
                return AjaxJson.success("删除成功");
            }
            return AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除提现金额配置失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }
}

