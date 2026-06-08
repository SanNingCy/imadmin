package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/im/friend", "/friend/friend"})
public class ImFriendViewController {

    @RequiresPermissions("friend:friend:list")
    @GetMapping()
    public String friend() {
        return "im/friend/friend";
    }
}
