package com.web4x.web.controller.social.content;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 朋友圈动态管理（兼容 IM 菜单 path {@code /moments}）。
 * 数据接口走 web4x-im {@code /dy/dy/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/moments",
        "/social/content/moments", "/content/moments"
})
public class SocialContentMomentsViewController {

    @RequiresPermissions("social:content:moments:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/content/moments";
    }
}
