package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广场朋友圈 - 互动消息（兼容 IM 菜单 path {@code /piamom/notify/list}）。
 */
@Controller
@RequestMapping("/piamom/notify/list")
public class PiamomNotifyListViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/piamom/notify/list";
    }
}
