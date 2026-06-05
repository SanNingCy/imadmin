package com.seekweb4.chat.modules.creditScore.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreTypeConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreTypeConfig;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreTypeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/admin/creditScore/typeConfig", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "信用分后台管理-类型配置")
public class CreditScoreTypeConfigAdminController extends BaseController {

    @Resource
    private CreditScoreTypeConfigService creditScoreTypeConfigService;

    @ApiOperation("分页查询信用分类型配置")
    @GetMapping("/page")
    public AjaxJson page(CreditScoreTypeConfigQueryDto queryDto) {
        try {
            Page<CreditScoreTypeConfig> page = creditScoreTypeConfigService.page(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询信用分类型配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("查询全部信用分类型（含启用/禁用）")
    @GetMapping("/listAll")
    public AjaxJson listAll() {
        try {
            List<CreditScoreTypeConfig> list = creditScoreTypeConfigService.listAllTypes();
            return AjaxJson.success().put("list", list);
        } catch (Exception e) {
            log.error("查询全部信用分类型失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("查询启用中的信用分类型")
    @GetMapping("/listEnabled")
    public AjaxJson listEnabled() {
        try {
            List<CreditScoreTypeConfig> list = creditScoreTypeConfigService.listEnabledTypes();
            return AjaxJson.success().put("list", list);
        } catch (Exception e) {
            log.error("查询启用信用分类型失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询信用分类型配置")
    @GetMapping("/queryById/{id}")
    public AjaxJson getById(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            CreditScoreTypeConfig config = creditScoreTypeConfigService.getById(id);
            if (config == null) {
                return AjaxJson.error("类型配置不存在");
            }
            return AjaxJson.success().put("config", config);
        } catch (Exception e) {
            log.error("查询信用分类型配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增信用分类型配置")
    @PostMapping("/save")
    public AjaxJson save(@RequestBody CreditScoreTypeConfig config) {
        try {
            boolean ok = creditScoreTypeConfigService.save(config);
            return ok ? AjaxJson.success("保存成功") : AjaxJson.error("保存失败");
        } catch (Exception e) {
            log.error("新增信用分类型配置失败", e);
            return AjaxJson.error("保存失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新信用分类型配置")
    @PutMapping("/update")
    public AjaxJson update(@RequestBody CreditScoreTypeConfig config) {
        try {
            if (config.getId() == null) {
                return AjaxJson.error("id不能为空");
            }
            boolean ok = creditScoreTypeConfigService.update(config);
            return ok ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新信用分类型配置失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("启用/禁用信用分类型配置")
    @PutMapping("/updateStatus")
    public AjaxJson updateStatus(@ApiParam("主键ID") @RequestParam Long id,
                                 @ApiParam("状态 1:启用 0:禁用") @RequestParam Integer status,
                                 @ApiParam("更新人id") @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = creditScoreTypeConfigService.updateStatus(id, status, updateBy);
            return ok ? AjaxJson.success("操作成功") : AjaxJson.error("操作失败");
        } catch (Exception e) {
            log.error("更新信用分类型配置状态失败", e);
            return AjaxJson.error("操作失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除信用分类型配置（逻辑删除）")
    @DeleteMapping("/remove")
    public AjaxJson remove(@ApiParam("主键ID") @RequestParam Long id,
                           @ApiParam("更新人id") @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = creditScoreTypeConfigService.remove(id, updateBy);
            return ok ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除信用分类型配置失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }
}

