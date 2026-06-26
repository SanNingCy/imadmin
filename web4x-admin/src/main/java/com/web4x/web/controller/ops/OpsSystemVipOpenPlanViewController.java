package com.web4x.web.controller.ops;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 会员开通套餐页面。
 * 菜单：平台运营与配置 -> 系统配置 -> 会员开通套餐
 */
@Controller
@RequestMapping({
        "/ops/system/vipOpenPlan", "/system/vipOpenPlan", "/vipOpenPlan"
})
public class OpsSystemVipOpenPlanViewController {

    @RequiresPermissions("ops:system:vip-open-plan:view")
    @GetMapping(value = {"", "/"})
    public String vipOpenPlan() {
        return "im/system/vip-open-plan";
    }
}
