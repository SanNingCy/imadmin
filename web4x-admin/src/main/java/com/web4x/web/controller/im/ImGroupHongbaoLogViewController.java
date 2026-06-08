package com.web4x.web.controller.im;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 群红包领取明细 - 若依 Thymeleaf 页面入口（数据接口走 /grouphongbaolog/groupHongbaoLog/*）
 */
@Controller
@RequestMapping({"/claim-logs", "/grouphongbaolog/groupHongbaoLog"})
public class ImGroupHongbaoLogViewController {

    private static final String PREFIX = "im/grouphongbaolog";

    @GetMapping()
    public String groupHongbaoLog() {
        return PREFIX + "/groupHongbaoLog";
    }
}
