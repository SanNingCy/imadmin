package com.seekweb4.chat.modules.memberCharacterTags.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.memberCharacterTags.entity.DTO.MemberCharacterTagsDTO;
import com.seekweb4.chat.modules.memberCharacterTags.entity.MemberCharacterTags;
import com.seekweb4.chat.modules.memberCharacterTags.service.MemberCharacterTagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/admin/character",produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "人设标签后台管理")
public class MemberCharacterTagsController extends BaseController {
    @Autowired
    private MemberCharacterTagsService memberCharacterTagsService;

    @ApiOperation("分页查询人设标签")
    @GetMapping("/tags/page")
    public AjaxJson getInterestPage(
           MemberCharacterTagsDTO memberCharacterTagsDTO
    ) {

        try {
            Page<MemberCharacterTags> page = memberCharacterTagsService.selectAdminPageList(memberCharacterTagsDTO);
            return AjaxJson.success().put("page", page);
        } catch (Exception e) {
            log.error("分页查询人设标签失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("根据ID查询人设标签")
    @RequiresPermissions(value = {"tagsInterest:tagsInterest:view", "tagsInterest:tagsInterest:add", "tagsInterest:tagsInterest:edit"}, logical = Logical.OR)
    @GetMapping("/tags/{id}")
    public AjaxJson getInterestById(@ApiParam("主键ID") @PathVariable Long id) {
        try {
            MemberCharacterTags memberCharacterTags = memberCharacterTagsService.selectCharacterTagsByID(id);
            return AjaxJson.success().put("tagsId", memberCharacterTags);
        } catch (Exception e) {
            log.error("根据ID查询人设标签失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("添加人设标签")
    @RequiresPermissions("tagsInterest:tagsInterest:add")
    @PostMapping("/tags/save")
    public AjaxJson addInterest(@RequestBody MemberCharacterTags memberCharacterTags) {
        try {
            memberCharacterTagsService.addCharacterTags(memberCharacterTags);
            return AjaxJson.success("添加成功");
        } catch (Exception e) {
            log.error("添加人设标签失败", e);
            return AjaxJson.error("添加失败：" + e.getMessage());
        }
    }

    @ApiOperation("修改人设标签")
    @RequiresPermissions("tagsInterest:tagsInterest:edit")
    @PutMapping("/tags/update")
    public AjaxJson updateInterest(@RequestBody MemberCharacterTags memberCharacterTags) {
        try {
            memberCharacterTagsService.updateCharacterTags(memberCharacterTags);
            return AjaxJson.success("修改成功");
        } catch (Exception e) {
            log.error("修改人设标签失败", e);
            return AjaxJson.error("修改失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除人设标签")
    @RequiresPermissions("tagsInterest:tagsInterest:remove")
    @DeleteMapping("/tags/delete")
    public AjaxJson deleteInterest(@ApiParam("主键ID") @RequestParam Long id) {
        try {
            memberCharacterTagsService.deleteCharacterTags(id);
            return AjaxJson.success("删除成功");
        } catch (Exception e) {
            log.error("删除人设标签失败", e);
            return AjaxJson.error("删除失败：" + e.getMessage());
        }
    }
}
