package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广场朋友圈 - 广场热门配置（兼容 IM 菜单 path {@code /piamom/square/hot-config}）。
 */
@Controller
@RequestMapping("/piamom/square/hot-config")
public class PiamomSquareHotConfigViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/piamom/square/hot-config";
    }
}
