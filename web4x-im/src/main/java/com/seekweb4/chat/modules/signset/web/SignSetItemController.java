package com.seekweb4.chat.modules.signset.web;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.signset.entity.SignSetItem;
import com.seekweb4.chat.modules.signset.service.SignSetItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 连签额外奖励配置子项Controller（仅提供新增/修改/删除接口）
 *
 * 不修改现有签到接口，仅在此新增对 t_sign_set_item 的操作。
 */
@Slf4j
@RestController
@RequestMapping(value = "/signset/signSet/item", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "签到奖励配置-连签额外奖励项")
public class SignSetItemController {

    @Resource
    private SignSetItemService signSetItemService;

    @ApiOperation("新增连签额外奖励项")
//    @RequiresPermissions(value = {"signset:signSet:add", "signset:signSet:edit"}, logical = Logical.OR)
    @PostMapping("/save")
    public AjaxJson save(@RequestBody SignSetItem item) {
        try {
            boolean ok = signSetItemService.add(item);
            return ok ? AjaxJson.success("保存成功") : AjaxJson.error("保存失败");
        } catch (IllegalArgumentException e) {
            return AjaxJson.error(e.getMessage());
        } catch (Exception e) {
            log.error("新增连签额外奖励项失败", e);
            return AjaxJson.error("保存失败：" + e.getMessage());
        }
    }

    @ApiOperation("修改连签额外奖励项")
//    @RequiresPermissions(value = {"signset:signSet:add", "signset:signSet:edit"}, logical = Logical.OR)
    @PutMapping("/update")
    public AjaxJson update(@RequestBody SignSetItem item) {
        try {
            if (item == null || StringUtils.isBlank(item.getId())) {
                return AjaxJson.error("主键ID不能为空");
            }
            boolean ok = signSetItemService.update(item);
            return ok ? AjaxJson.success("更新成功") : AjaxJson.error("更新失败");
        } catch (IllegalArgumentException e) {
            return AjaxJson.error(e.getMessage());
        } catch (Exception e) {
            log.error("修改连签额外奖励项失败", e);
            return AjaxJson.error("更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除连签额外奖励项")
//    @RequiresPermissions("signset:signSet:del")
    @DeleteMapping("/delete")
    public AjaxJson delete(@ApiParam("主键ID") @RequestParam String id) {
        try {
            if (StringUtils.isBlank(id)) {
                return AjaxJson.error("主键ID不能为空");
            }
            boolean ok = signSetItemService.deleteById(id);
            return ok ? AjaxJson.success("删除成功") : AjaxJson.error("删除失败");
        } catch (IllegalArgumentException e) {
            return AjaxJson.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除连签额外奖励项失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }
}

