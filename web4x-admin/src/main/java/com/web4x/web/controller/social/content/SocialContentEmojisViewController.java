package com.web4x.web.controller.social.content;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 表情包管理（兼容 IM 菜单 path {@code /emojis}）。
 * 数据接口走 web4x-im {@code /emoji/emoji/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/emojis",
        "/social/content/emojis", "/content/emojis"
})
public class SocialContentEmojisViewController {

    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/content/emojis";
    }
}
