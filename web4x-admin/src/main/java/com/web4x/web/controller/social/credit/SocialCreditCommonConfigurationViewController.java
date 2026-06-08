package com.web4x.web.controller.social.credit;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 信用分公共配置（兼容 IM 菜单 path {@code /common-configuration}）。
 */
@Controller
@RequestMapping({
        "/common-configuration",
        "/social/credit/common-configuration", "/credit/common-configuration",
        "/creditScore/avatarDisplay", "/avatarDisplay", "/admin/creditScore/avatarDisplay"
})
public class SocialCreditCommonConfigurationViewController {

    @RequiresPermissions("social:credit:common-configuration:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/credit/common-configuration";
    }
}
