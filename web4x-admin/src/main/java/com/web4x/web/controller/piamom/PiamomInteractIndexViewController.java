package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 互动消息目录页重定向（菜单父级 {@code /piamom/interact} 无独立页面时跳转至列表）。
 */
@Controller
@RequestMapping("/piamom/interact")
public class PiamomInteractIndexViewController {

    @GetMapping(value = {"", "/"})
    public String index() {
        return "redirect:/piamom/notify/list";
    }
}
