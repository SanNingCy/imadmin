package com.seekweb4.chat.modules.creditScore.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreAvatarDisplayConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreAvatarDisplayConfig;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreAvatarDisplayConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@Slf4j
@RestController
@RequestMapping(value = "/admin/creditScore/avatarDisplay", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "信用分后台管理-头像角标展示配置")
public class CreditScoreAvatarDisplayAdminController extends BaseController {

    @Resource
    private CreditScoreAvatarDisplayConfigService creditScoreAvatarDisplayConfigService;

    @ApiOperation("分页查询头像角标展示配置")
    @GetMapping("/page")
    public AjaxJson page(CreditScoreAvatarDisplayConfigQueryDto queryDto) {
        try {
            Page<CreditScoreAvatarDisplayConfig> page = creditScoreAvatarDisplayConfigService.page(queryDto);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询头像角标展示配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("查询库中最新一条头像角标配置")
    @GetMapping("/getCurrent")
    public AjaxJson getCurrent() {
        try {
            CreditScoreAvatarDisplayConfig config = creditScoreAvatarDisplayConfigService.getCurrent();
            return AjaxJson.success().put("config", config);
        } catch (Exception e) {
            log.error("查询头像角标展示配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("查询当前对外生效配置")
    @GetMapping("/getEffective")
    public AjaxJson getEffective() {
        try {
            CreditScoreAvatarDisplayConfig config = creditScoreAvatarDisplayConfigService.getEffectiveCurrent();
            return AjaxJson.success().put("config", config);
        } catch (Exception e) {
            log.error("查询生效头像角标展示配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询头像角标展示配置")
    @GetMapping("/queryById/{id}")
    public AjaxJson getById(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            CreditScoreAvatarDisplayConfig config = creditScoreAvatarDisplayConfigService.getById(id);
            if (config == null) {
                return AjaxJson.error("配置不存在");
            }
            return AjaxJson.success().put("config", config);
        } catch (Exception e) {
            log.error("查询头像角标展示配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("新增头像角标展示配置")
    @PostMapping("/save")
    public AjaxJson save(@RequestBody CreditScoreAvatarDisplayConfig config) {
        try {
            boolean ok = creditScoreAvatarDisplayConfigService.save(config);
            return ok ? AjaxJson.success("保存成功") : AjaxJson.error("保存失败");
        } catch (Exception e) {
            log.error("新增头像角标展示配置失败", e);
            return AjaxJson.error("保存失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新头像角标展示配置")
    @PutMapping("/update")
    public AjaxJson update(@RequestBody CreditScoreAvatarDisplayConfig config) {
        try {
            if (config.getId() == null) {
                return AjaxJson.error("id不能为空");
            }
            boolean ok = creditScoreAvatarDisplayConfigService.update(config);
            return ok ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (Exception e) {
            log.error("更新头像角标展示配置失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除头像角标展示配置（逻辑删除）")
    @DeleteMapping("/remove")
    public AjaxJson remove(@ApiParam("主键ID") @RequestParam Long id,
                           @ApiParam("更新人id") @RequestParam(required = false) String updateBy) {
        try {
            boolean ok = creditScoreAvatarDisplayConfigService.remove(id, updateBy);
            return ok ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (Exception e) {
            log.error("删除头像角标展示配置失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }
}
