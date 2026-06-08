package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 充值记录 - 若依 Thymeleaf 页面入口（数据接口走 /rechagelog/rechageLog/*）
 */
@Controller
@RequestMapping({"/deposit", "/rechagelog/rechageLog"})
public class ImRechageLogViewController {

    private static final String PREFIX = "im/rechagelog";

    @RequiresPermissions("rechagelog:rechageLog:list")
    @GetMapping()
    public String rechageLog() {
        return PREFIX + "/rechageLog";
    }
}