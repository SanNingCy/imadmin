package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 兼容若依“版本更新管理”菜单路径。
 *
 * 仅用于渲染页面，不改动 chat-ops（web4x-im）后端接口与数据库结构。
 */
@Controller
@RequestMapping({"/ops/content/version", "/version"})
public class OpsContentVersionViewController {

    private static final String VIEW_NAME = "im/upgrade/upgrade";

    @RequiresPermissions("ops:content:version:view")
    @GetMapping
    public String version() {
        return VIEW_NAME;
    }
}

