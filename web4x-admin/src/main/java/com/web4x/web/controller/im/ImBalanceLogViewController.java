package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 用户余额明细 - 若依 Thymeleaf 页面入口（数据接口走 /balancelog/balanceLog/*）
 */
@Controller
@RequestMapping({"/balance", "/balancelog/balanceLog"})
public class ImBalanceLogViewController {

    private static final String PREFIX = "im/balancelog";

    @RequiresPermissions("asset:fund:balance:view")
    @GetMapping()
    public String balanceLog() {
        return PREFIX + "/balanceLog";
    }
}