package com.web4x.web.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 内部转账交易记录 - 若依 Thymeleaf 页面入口（数据接口走 /admin/asset/paymentTransactionIm/*）
 */
@Controller
@RequestMapping({"/im-streams", "/admin/asset/paymentTransactionIm"})
public class ImPaymentTransactionImViewController {

    private static final String PREFIX = "im/paymenttransactionim";

    @GetMapping()
    public String paymentTransactionIm() {
        return PREFIX + "/paymentTransactionIm";
    }
}
