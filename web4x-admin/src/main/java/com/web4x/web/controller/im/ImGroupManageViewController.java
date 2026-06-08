package com.web4x.web.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 群组管理子页面 - 若依 Thymeleaf 页面入口（数据接口走 web4x-im 已有 REST API）
 */
@Controller
public class ImGroupManageViewController {

    private static final String PREFIX = "im/group/manage";

    @GetMapping({"/info", "/info/"})
    public String info() {
        return PREFIX + "/info";
    }

    @GetMapping({"/member", "/member/"})
    public String member() {
        return PREFIX + "/member";
    }

    @GetMapping({"/complaints", "/complaints/"})
    public String complaints() {
        return PREFIX + "/complaints";
    }

    @GetMapping({"/upgrade-logs", "/upgrade-logs/"})
    public String upgradeLogs() {
        return PREFIX + "/upgrade-logs";
    }

    @GetMapping({"/welcome", "/welcome/"})
    public String welcome() {
        return PREFIX + "/welcome";
    }

    @GetMapping({"/scheduled-message", "/scheduled-message/"})
    public String scheduledMessage() {
        return PREFIX + "/scheduled-message";
    }
}
