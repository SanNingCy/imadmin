package com.seekweb4.chat.modules.member.web;

import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.service.TwoFactorService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 谷歌验证管理Controller
 * @author system
 * @version 2024-12-10
 */
@RestController
@RequestMapping(value = "/member/twoFactor")
public class TwoFactorController extends BaseController {

    @Autowired
    private TwoFactorService twoFactorService;

    /**
     * 已启用谷歌验证的用户列表数据（分页）
     */
    @ApiLog("查询已启用谷歌验证的用户列表")
//    @RequiresPermissions("member:twoFactor:list")
    @GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson list(Member member, HttpServletRequest request, HttpServletResponse response) {
        Page<Member> page = twoFactorService.findPage(new Page<Member>(request, response), member);
        return AjaxJson.success().put("page", page);
    }

    /**
     * 重置用户的谷歌验证码
     */
    @ApiLog("重置谷歌验证码")
    @RequiresPermissions("risk:security:google-verify:reset")
    @PostMapping(value = "reset", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson reset(@RequestParam(value = "id", required = true) String id) {
        if (StringUtils.isBlank(id)) {
            return AjaxJson.error("用户ID不能为空");
        }
        boolean result = twoFactorService.resetTwoFactorCode(id);
        if (result) {
            return AjaxJson.success("重置谷歌验证码成功");
        } else {
            return AjaxJson.error("重置谷歌验证码失败");
        }
    }
}

