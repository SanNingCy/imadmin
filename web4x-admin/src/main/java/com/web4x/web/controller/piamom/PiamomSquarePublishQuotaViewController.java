package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广场朋友圈 - 广场发帖信用分额度（兼容 IM 菜单 path {@code /piamom/square/publish-quota}）。
 */
@Controller
@RequestMapping("/piamom/square/publish-quota")
public class PiamomSquarePublishQuotaViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/piamom/square/publish-quota";
    }
}
