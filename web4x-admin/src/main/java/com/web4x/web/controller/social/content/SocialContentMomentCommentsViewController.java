package com.web4x.web.controller.social.content;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 动态评论管理（兼容 IM 菜单 path {@code /moment-comments}）。
 * 数据接口走 web4x-im {@code /dyomm/dyComm/*}，不改动业务后端。
 */
@Controller
@RequestMapping({
        "/moment-comments",
        "/social/content/moment-comments", "/content/moment-comments"
})
public class SocialContentMomentCommentsViewController {

    @RequiresPermissions("social:content:moment-comments:view")
    @GetMapping(value = {"", "/"})
    public String page() {
        return "im/content/moment-comments";
    }
}
