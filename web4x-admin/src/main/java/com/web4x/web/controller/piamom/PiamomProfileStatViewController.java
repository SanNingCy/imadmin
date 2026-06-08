package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广场朋友圈 - 用户主页统计（兼容 IM 菜单 path {@code /piamom/profile/stat}）。
 */
@Controller
@RequestMapping("/piamom/profile/stat")
public class PiamomProfileStatViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/piamom/profile/stat";
    }
}
