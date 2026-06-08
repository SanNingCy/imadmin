package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广场朋友圈 - 广场点赞列表（兼容 IM 菜单 path {@code /piamom/square/like}）。
 */
@Controller
@RequestMapping("/piamom/square/like")
public class PiamomSquareLikeViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/piamom/square/like";
    }
}
