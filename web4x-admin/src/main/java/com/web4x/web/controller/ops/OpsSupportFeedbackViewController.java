package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/ops/support/feedback", "/feedback"})
public class OpsSupportFeedbackViewController {

    @RequiresPermissions("ops:support:feedback:view")
    @GetMapping
    public String feedback() {
        return "im/support/feedback";
    }
}
