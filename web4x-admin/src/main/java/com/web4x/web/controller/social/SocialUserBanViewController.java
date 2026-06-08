package com.web4x.web.controller.social;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户封禁管理（兼容 IM 菜单 path）。
 * 数据接口走 web4x-im 已有 {@code /member/member/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/social/user/ban", "/user/ban", "/ban"
})
public class SocialUserBanViewController {

    private static final String VIEW_NAME = "im/social/user-ban";

    @RequiresPermissions("social:user:ban:view")
    @GetMapping(value = {"", "/"})
    public String ban() {
        return VIEW_NAME;
    }
}
