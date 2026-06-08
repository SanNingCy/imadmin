package com.web4x.web.controller.social.security;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 会员码管理（兼容 IM 菜单 path {@code /vip-code}）。
 * 数据接口走 web4x-im {@code /vipcode/vipCode/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/vip-code",
        "/social/security/vip-code", "/security/vip-code"
})
public class SocialSecurityVipCodeViewController {

    @RequiresPermissions("social:security:vip-code:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/security/vip-code";
    }
}
