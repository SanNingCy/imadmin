package com.web4x.web.controller.social.security;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 密保问题管理（兼容 IM 菜单 path {@code /qa}）。
 * 数据接口走 web4x-im {@code /mibaofaq/mibaoFaq/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/qa",
        "/social/security/qa", "/security/qa"
})
public class SocialSecurityQaViewController {

    @RequiresPermissions("social:security:qa:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/security/qa";
    }
}
