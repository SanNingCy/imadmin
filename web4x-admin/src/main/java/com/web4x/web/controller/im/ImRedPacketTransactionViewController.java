package com.web4x.web.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 红包交易记录 - 若依 Thymeleaf 页面入口（数据接口走 /redPacketTransaction/redPacketTransaction/*）
 */
@Controller
@RequestMapping({"/packet", "/redPacketTransaction/redPacketTransaction"})
public class ImRedPacketTransactionViewController {

    private static final String PREFIX = "im/redpacket";

    @GetMapping()
    public String redPacketTransaction() {
        return PREFIX + "/redPacketTransaction";
    }
}
