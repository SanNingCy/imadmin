package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广场朋友圈 - 举报类型配置（兼容 IM 菜单 path {@code /piamom/report/config}）。
 */
@Controller
@RequestMapping("/piamom/report/config")
public class PiamomReportConfigViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/piamom/report/config";
    }
}
