package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广场朋友圈 - 广场列表（兼容 IM 菜单 path {@code /piamom/square/list}）。
 * 数据接口走 web4x-im {@code /admin/piamom/square/*}，不改动业务后端。
 */
@Controller
@RequestMapping("/piamom/square/list")
public class PiamomSquareListViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/piamom/square/list";
    }
}
