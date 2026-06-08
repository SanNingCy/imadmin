package com.web4x.web.controller.piamom;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.web4x.common.utils.ShiroUtils;

/**
 * 广场朋友圈 - 举报记录审核（兼容 IM 菜单 path {@code /piamom/report/record}）。
 */
@Controller
@RequestMapping("/piamom/report/record")
public class PiamomReportRecordViewController {

    @GetMapping(value = {"", "/"})
    public String page(Model model) {
        Long userId = ShiroUtils.getUserId();
        model.addAttribute("auditUserId", userId != null ? String.valueOf(userId) : "");
        return "im/piamom/report/record";
    }
}
