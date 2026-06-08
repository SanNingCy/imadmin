package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 提现申请/审核 - 若依 Thymeleaf 页面入口（数据接口走 /admin/asset/withdrawApply/*）
 */
@Controller
@RequestMapping({"/withdrawApply", "/admin/asset/withdrawApply", "/asset/fund/withdraw", "/asset/withdrawApply"})
public class ImWithdrawApplyViewController {

    private static final String PREFIX = "im/withdrawapply";

    @RequiresPermissions("asset:fund:withdraw:view")
    @GetMapping
    public String withdrawApply() {
        return PREFIX + "/withdrawApply";
    }
}
