package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/ops/support/faq", "/faq"})
public class OpsSupportFaqViewController {

    @RequiresPermissions("ops:support:faq:view")
    @GetMapping
    public String faq() {
        return "im/support/faq";
    }
}
