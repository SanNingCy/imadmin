package com.web4x.web.controller.social.security;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 登录记录审计（兼容 IM 菜单 path {@code /login-audit}）。
 * 数据接口走 web4x-im {@code /loginlog/loginLog/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/login-audit",
        "/social/security/login-audit", "/security/login-audit"
})
public class SocialSecurityLoginAuditViewController {

    @RequiresPermissions("social:security:login-audit:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/security/login-audit";
    }
}
