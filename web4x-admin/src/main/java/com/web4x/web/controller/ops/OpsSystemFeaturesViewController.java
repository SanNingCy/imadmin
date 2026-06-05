package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 功能开关管理页面。
 * 兼容 IM 菜单 path：/ops/system/features、/features、旧版 /buttonConfig/buttonConfig。
 */
@Controller
@RequestMapping({
        "/ops/system/features", "/system/features", "/features",
        "/buttonConfig/buttonConfig"
})
public class OpsSystemFeaturesViewController {

    @RequiresPermissions("ops:system:features:view")
    @GetMapping(value = {"", "/"})
    public String features() {
        return "im/system/features";
    }
}
