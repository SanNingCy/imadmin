package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/ops/support/agreement", "/agreement"})
public class OpsSupportAgreementViewController {

    @RequiresPermissions("ops:support:agreement:view")
    @GetMapping
    public String agreement() {
        return "im/support/agreement";
    }
}
