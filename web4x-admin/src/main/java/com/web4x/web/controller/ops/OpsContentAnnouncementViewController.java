package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 公告管理配置页面（兼容 IM 菜单 path：/announcement 与 /ops/content/announcement）。
 * 数据接口走 web4x-im 已有 {@code /notif/admin/*}，不改动业务后端。
 */
@Controller
@RequestMapping({"/ops/content/announcement", "/announcement"})
public class OpsContentAnnouncementViewController {

    private static final String VIEW_NAME = "im/announcement/announcement";

    @RequiresPermissions("ops:content:announcement:view")
    @GetMapping
    public String announcement() {
        return VIEW_NAME;
    }
}
