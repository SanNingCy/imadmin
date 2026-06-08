package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/im/group", "/group/group"})
public class ImGroupViewController {

    @RequiresPermissions("group:group:list")
    @GetMapping()
    public String group() {
        return "im/group/group";
    }
}
