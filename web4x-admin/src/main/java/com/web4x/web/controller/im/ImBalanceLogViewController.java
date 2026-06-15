package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.Logical;
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

    @RequiresPermissions(value = {
            "asset:fund:balance:view",
            "asset:fund:withdraw:view",
            "asset:fund:payment:view",
            "rechagelog:rechageLog:list",
            "asset:fund:deposit:approved",
            "asset:fund:deposit:reject",
            "asset:fund:deposit:lock"
    }, logical = Logical.OR)
    @GetMapping()
    public String balanceLog() {
        return PREFIX + "/balanceLog";
    }
}