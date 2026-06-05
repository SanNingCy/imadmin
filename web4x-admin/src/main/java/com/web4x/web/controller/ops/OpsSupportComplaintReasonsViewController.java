package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/ops/support/complaint-reasons", "/complaint-reasons", "/reason"})
public class OpsSupportComplaintReasonsViewController {

    @RequiresPermissions("ops:support:complaint-reasons:view")
    @GetMapping
    public String complaintReasons() {
        return "im/support/complaint-reasons";
    }
}
