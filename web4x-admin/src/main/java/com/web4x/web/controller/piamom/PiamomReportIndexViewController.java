package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 举报管理目录页重定向（菜单父级 {@code /piamom/report} 无独立页面时跳转至类型配置）。
 */
@Controller
@RequestMapping("/piamom/report")
public class PiamomReportIndexViewController {

    @GetMapping(value = {"", "/"})
    public String index() {
        return "redirect:/piamom/report/config";
    }
}
