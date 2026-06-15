package com.web4x.web.controller.social;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 好友关系管理（兼容 IM 菜单 path）。
 * 数据接口走 web4x-im 已有 {@code /friend/friend/*}，不改动业务后端。
 * 保留 {@link com.web4x.web.controller.im.ImFriendViewController} 的 /im/friend 简易页不受影响。
 */
@Controller
@RequestMapping({
        "/social/user/friends", "/user/friends", "/friends",
        "/friend/friend", "/friend"
})
public class SocialUserFriendsViewController {

    private static final String VIEW_NAME = "im/social/user-friends";

    @RequiresPermissions(value = {
            "social:user:friends:view",
            "social:user:friends:add",
            "social:user:friends:delete",
            "friend:friend:list", "friend:friend:view"
    }, logical = Logical.OR)
    @GetMapping(value = {"", "/"})
    public String friends() {
        return VIEW_NAME;
    }
}
