package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 链上支付订单 - 若依 Thymeleaf 页面入口（数据接口走 /admin/asset/chainPayOrder/*）
 */
@Controller
@RequestMapping({
        "/chainPayOrder",
        "/admin/asset/chainPayOrder",
        "/asset/fund/chainPayOrder",
        "/asset/chainPayOrder"
})
public class ImChainPayOrderViewController {

    private static final String PREFIX = "im/chainpayorder";

    @RequiresPermissions("asset:fund:chain-pay:view")
    @GetMapping({"", "/"})
    public String chainPayOrder() {
        return PREFIX + "/chainPayOrder";
    }
}
