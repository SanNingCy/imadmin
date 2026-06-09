package com.web4x.web.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 会议管理页面 - 若依 Thymeleaf 入口（数据接口走 /admin/live/*）
 */
@Controller
public class ImLiveViewController {

    private static final String PREFIX = "im/live";

    @GetMapping({"/duration", "/duration/"})
    public String duration() {
        return PREFIX + "/duration";
    }

    @GetMapping({"/personnel", "/personnel/"})
    public String tier() {
        return PREFIX + "/tier";
    }

    @GetMapping({"/order", "/order/"})
    public String order() {
        return PREFIX + "/order";
    }

    @GetMapping({"/token-config", "/token-config/"})
    public String roomConfig() {
        return PREFIX + "/room-config";
    }

    @GetMapping({"/price", "/price/"})
    public String fixedPrice() {
        return PREFIX + "/fixed-price";
    }

    @GetMapping({"/usdt-price", "/usdt-price/"})
    public String usdtPrice() {
        return PREFIX + "/usdt-price";
    }
}
