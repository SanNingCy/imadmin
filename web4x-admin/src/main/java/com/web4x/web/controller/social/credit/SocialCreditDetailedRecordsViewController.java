package com.web4x.web.controller.social.credit;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 信用分明细记录（兼容 IM 菜单 path {@code /detailed-records}）。
 */
@Controller
@RequestMapping({
        "/detailed-records",
        "/social/credit/detailed-records", "/credit/detailed-records",
        "/creditScore/log", "/admin/creditScore/log"
})
public class SocialCreditDetailedRecordsViewController {

    @RequiresPermissions("social:credit:detailed-records:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/credit/detailed-records";
    }
}
