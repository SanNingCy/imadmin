package com.web4x.web.controller.social.credit;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 信用分类型配置（兼容 IM 菜单 path {@code /type}）。
 */
@Controller
@RequestMapping({
        "/type",
        "/social/credit/type", "/credit/type",
        "/creditScore/typeConfig", "/admin/creditScore/typeConfig"
})
public class SocialCreditTypeViewController {

    @RequiresPermissions("social:credit:type:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/credit/type";
    }
}
