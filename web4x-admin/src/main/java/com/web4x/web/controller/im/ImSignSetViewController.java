package com.web4x.web.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 签到奖励配置 - 若依 Thymeleaf 页面入口（数据接口走 /signset/signSet/*）
 */
@Controller
@RequestMapping({"/signin-reward", "/signset/signSet"})
public class ImSignSetViewController {

    private static final String PREFIX = "im/signset";

    @GetMapping()
    public String signSet() {
        return PREFIX + "/signSet";
    }
}
