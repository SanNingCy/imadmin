package com.web4x.web.controller.im;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * IM 移动端用户 - 若依 Thymeleaf 页面入口（数据接口仍走 /member/member/*）
 */
@Controller
@RequestMapping({"/im/member", "/member/member"})
public class ImMemberViewController {

    private static final String PREFIX = "im/member";

    @RequiresPermissions("member:member:list")
    @GetMapping()
    public String member() {
        return PREFIX + "/member";
    }
}
