package com.web4x.web.controller.risk.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 封禁记录查询（兼容 IM 菜单 path {@code /ban-logs}）。
 * 数据接口走 web4x-im {@code /member/member/banList} 等，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/ban-logs",
        "/risk/security/ban-logs", "/risk/ban-logs"
})
public class RiskSecurityBanLogsViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/risk/security/ban-logs";
    }
}
