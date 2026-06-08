package com.web4x.web.controller.social.credit;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户信用分管理（兼容 IM 菜单 path {@code /management}）。
 */
@Controller
@RequestMapping({
        "/management",
        "/social/credit/management", "/credit/management",
        "/creditScore/user", "/admin/creditScore/user"
})
public class SocialCreditManagementViewController {

    @RequiresPermissions("social:credit:management:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/credit/management";
    }
}
