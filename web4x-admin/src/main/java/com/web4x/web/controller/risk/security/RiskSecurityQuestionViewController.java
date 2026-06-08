package com.web4x.web.controller.risk.security;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户密保问题（兼容 IM 菜单 path {@code /security-question}）。
 * 数据接口走 web4x-im {@code /member/mibao/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/security-question",
        "/risk/security/security-question", "/risk/security-question"
})
public class RiskSecurityQuestionViewController {

    @RequiresPermissions("risk:security:security-question:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/risk/security/security-question";
    }
}
