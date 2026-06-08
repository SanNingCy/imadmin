package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广场朋友圈 - 朋友圈评论列表（兼容 IM 菜单 path {@code /piamom/moment/comment}）。
 * 数据接口走 web4x-im {@code /admin/piamom/moment/comment/*}，不改动业务后端。
 */
@Controller
@RequestMapping("/piamom/moment/comment")
public class PiamomMomentCommentViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/piamom/moment/comment";
    }
}
