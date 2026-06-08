package com.web4x.web.controller.risk.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 全局敏感词（兼容 IM 菜单 path {@code /sensitive-word}）。
 * 数据接口走 web4x-im {@code /admin/sensitive/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/sensitive-word",
        "/risk/security/sensitive-word", "/risk/sensitive-word"
})
public class RiskSecuritySensitiveWordViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/risk/security/sensitive-word";
    }
}
