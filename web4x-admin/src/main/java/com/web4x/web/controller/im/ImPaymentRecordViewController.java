package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 入金记录 - 若依 Thymeleaf 页面入口（数据接口走 /admin/asset/paymentRecord/*）
 */
@Controller
@RequestMapping({"/payment", "/admin/asset/paymentRecord", "/asset/paymentRecord", "/paymentRecord"})
public class ImPaymentRecordViewController {

    private static final String PREFIX = "im/paymentrecord";

    @RequiresPermissions("asset:fund:payment:view")
    @GetMapping()
    public String paymentRecord() {
        return PREFIX + "/paymentRecord";
    }
}