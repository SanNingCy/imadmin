package com.web4x.web.controller.social;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户列表（兼容 IM 菜单 path）。
 * 数据接口走 web4x-im 已有 {@code /member/member/*}，不改动业务后端。
 * 保留 {@link com.web4x.web.controller.im.ImMemberViewController} 的 /im/member 简易页不受影响。
 */
@Controller
@RequestMapping({
        "/social/user/list", "/user/list",
        "/member/member", "/list"
})
public class SocialUserListViewController {

    private static final String VIEW_NAME = "im/social/user-list";

    @RequiresPermissions(value = {
            "social:user:list:view",
            "member:member:list", "member:member:view"
    }, logical = Logical.OR)
    @GetMapping(value = {"", "/"})
    public String list() {
        return VIEW_NAME;
    }
}
