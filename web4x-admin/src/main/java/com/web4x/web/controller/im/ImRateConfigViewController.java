package com.web4x.web.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 平台费率设置 - 若依 Thymeleaf 页面入口（数据接口走 /admin/asset/rateConfig/*）
 */
@Controller
@RequestMapping({"/platform", "/admin/asset/rateConfig"})
public class ImRateConfigViewController {

    private static final String PREFIX = "im/rateconfig";

    @GetMapping()
    public String rateConfig() {
        return PREFIX + "/rateConfig";
    }
}
