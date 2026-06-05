package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 提现配置页面。
 * 兼容 IM 菜单 path：/ops/system/withdraw、/withdraw、/withdrawConfig 等。
 * 侧栏展开已用 index.js 精确匹配，避免与 /asset/fund/withdraw 误展开。
 */
@Controller
@RequestMapping({
        "/ops/system/withdraw", "/system/withdraw", "/withdrawConfig", "/withdraw"
})
public class OpsSystemWithdrawViewController {

    @RequiresPermissions("ops:system:withdraw:view")
    @GetMapping(value = {"", "/"})
    public String withdraw() {
        return "im/system/withdraw";
    }
}
