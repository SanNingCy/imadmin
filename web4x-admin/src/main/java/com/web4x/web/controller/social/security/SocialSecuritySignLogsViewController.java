package com.web4x.web.controller.social.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 签到记录（兼容 IM 菜单 path {@code /sign-logs}）。
 * 数据接口走 web4x-im {@code /signlog/signLog/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/sign-logs",
        "/social/security/sign-logs", "/security/sign-logs"
})
public class SocialSecuritySignLogsViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/security/sign-logs";
    }
}
