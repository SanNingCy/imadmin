package com.web4x.web.controller.social;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户注销申请（兼容 IM 菜单 path）。
 * 数据接口走 web4x-im 已有 {@code /zhuxiao/zhuxiao/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/social/user/cancel-apply", "/user/cancel-apply", "/cancel-apply",
        "/zhuxiao/zhuxiao", "/zhuxiao"
})
public class SocialUserCancelApplyViewController {

    private static final String VIEW_NAME = "im/social/user-cancel-apply";

    @RequiresPermissions(value = {
            "social:user:cancel-apply:view",
            "social:user:cancel-apply:approved",
            "social:user:cancel-apply:reject"
    }, logical = Logical.OR)
    @GetMapping(value = {"", "/"})
    public String cancelApply() {
        return VIEW_NAME;
    }
}
