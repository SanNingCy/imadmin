package com.web4x.web.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 出金交易记录 - 若依 Thymeleaf 页面入口（数据接口走 /admin/asset/paymentTransaction/*）
 */
@Controller
@RequestMapping({"/streams", "/admin/asset/paymentTransaction"})
public class ImPaymentTransactionViewController {

    private static final String PREFIX = "im/paymenttransaction";

    @GetMapping()
    public String paymentTransaction() {
        return PREFIX + "/paymentTransaction";
    }
}
