package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 基础平台配置页面。
 * 兼容 IM 菜单 path：/ops/system/base、/base、旧版 /customer/customer（与列表 API 路径不冲突）。
 */
@Controller
@RequestMapping({
        "/ops/system/base", "/system/base", "/base",
        "/customer/customer"
})
public class OpsSystemBaseViewController {

    @RequiresPermissions("ops:system:base:view")
    @GetMapping(value = {"", "/"})
    public String base() {
        return "im/system/base";
    }
}
