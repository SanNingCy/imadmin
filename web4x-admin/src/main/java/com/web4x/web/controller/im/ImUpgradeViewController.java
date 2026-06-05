package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 版本更新管理：若依 Thymeleaf 页面入口（数据接口走 /upgrade/upgrade/*）。
 *
 * 注意：仅负责渲染页面
 */
@Controller
@RequestMapping("/im/upgrade")
public class ImUpgradeViewController {

    private static final String VIEW_NAME = "im/upgrade/upgrade";

    @RequiresPermissions("ops:content:version:view")
    @GetMapping
    public String upgrade() {
        return VIEW_NAME;
    }
}

