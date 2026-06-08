package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广场朋友圈 - 广场评论列表（兼容 IM 菜单 path {@code /piamom/square/comment}）。
 */
@Controller
@RequestMapping("/piamom/square/comment")
public class PiamomSquareCommentViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/piamom/square/comment";
    }
}
