package com.web4x.web.controller.social.credit;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 信用分基础配置（兼容 IM 菜单 path，见 sys_menu_two）。
 * 数据接口走 web4x-im {@code /admin/creditScore/config/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/configuration",
        "/social/credit/configuration",
        "/credit/configuration"
})
public class SocialCreditConfigurationViewController {

    @RequiresPermissions("social:credit:configuration:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/credit/configuration";
    }
}
