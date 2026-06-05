package com.seekweb4.chat.modules.member.web;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.MibaoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 密保问题管理Controller
 * @author system
 * @version 2024-12-10
 */
@RestController
@RequestMapping(value = "/member/mibao")
public class MibaoController extends BaseController {

    @Autowired
    private MibaoService mibaoService;

    /**
     * 已设置密保问题的用户列表数据（分页）
     */
    @ApiLog("查询已设置密保问题的用户列表")
//    @RequiresPermissions("member:mibao:list")
    @GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson list(Member member, HttpServletRequest request, HttpServletResponse response) {
        Page<Member> page = mibaoService.findPage(new Page<Member>(request, response), member);
        return AjaxJson.success().put("page", page);
    }

    /**
     * 根据ID查询用户的密保问题
     */
    @ApiLog("根据ID查询用户密保问题")
    @RequiresPermissions("risk:security:security-question:view")
    @GetMapping(value = "queryById", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson queryById(@RequestParam("id") String id) {
        if (StringUtils.isBlank(id)) {
            return AjaxJson.error("用户ID不能为空");
        }
        Member member = mibaoService.getById(id);
        if (member == null) {
            return AjaxJson.error("用户不存在");
        }
		// 返回完整的用户对象，字段结构与分页列表中的单条记录一致
		return AjaxJson.success().put("member", member);
    }

    /**
     * 修改用户的密保问题
     */
    @ApiLog("修改用户密保问题")
    @RequiresPermissions("risk:security:security-question:edit")
    @PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson update(@RequestParam(value = "id", required = true) String id,
                          @RequestParam(value = "mbid", required = false) String mbid,
                          @RequestParam(value = "mbname", required = true) String mbname,
                          @RequestParam(value = "mbda", required = true) String mbda) {
        if (StringUtils.isBlank(id)) {
            return AjaxJson.error("用户ID不能为空");
        }
//        if (StringUtils.isBlank(mbid)) {
//            return AjaxJson.error("密保问题ID不能为空");
//        }
        if (StringUtils.isBlank(mbname)) {
            return AjaxJson.error("密保问题名称不能为空");
        }
        if (StringUtils.isBlank(mbda)) {
            return AjaxJson.error("密保答案不能为空");
        }
        boolean result = mibaoService.updateMibao(id, mbid, mbname, mbda);
        if (result) {
            return AjaxJson.success("修改密保问题成功");
        } else {
            return AjaxJson.error("修改密保问题失败");
        }
    }

    /**
     * 重置用户的密保问题
     */
    @ApiLog("重置用户密保问题")
    @RequiresPermissions("risk:security:security-question:reset")
    @PostMapping(value = "reset", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson reset(@RequestParam(value = "id", required = true) String id) {
        if (StringUtils.isBlank(id)) {
            return AjaxJson.error("用户ID不能为空");
        }
        boolean result = mibaoService.resetMibao(id);
        if (result) {
            return AjaxJson.success("重置密保问题成功");
        } else {
            return AjaxJson.error("重置密保问题失败");
        }
    }
}