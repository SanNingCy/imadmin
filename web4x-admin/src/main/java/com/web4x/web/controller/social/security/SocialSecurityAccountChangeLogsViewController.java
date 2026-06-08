package com.web4x.web.controller.social.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 账户变更日志（兼容 IM 菜单 path {@code /account-change-logs}）。
 * 数据接口走 web4x-im {@code /memberChangeLog/memberChangeLog/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/account-change-logs",
        "/social/security/account-change-logs", "/security/account-change-logs"
})
public class SocialSecurityAccountChangeLogsViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/security/account-change-logs";
    }
}
