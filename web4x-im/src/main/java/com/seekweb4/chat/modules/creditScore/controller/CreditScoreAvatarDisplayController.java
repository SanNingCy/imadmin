package com.seekweb4.chat.modules.creditScore.controller;

import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreAvatarDisplayConfig;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreAvatarDisplayConfigService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreAvatarBadgeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 客户端：头像信用分角标展示（配置与按用户解析），路径与 /api/** 移动端约定一致，走 JwtInterceptor。
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/creditScore/avatarDisplay", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "信用分-头像角标展示")
public class CreditScoreAvatarDisplayController extends BaseController {

    @Resource
    private CreditScoreAvatarDisplayConfigService creditScoreAvatarDisplayConfigService;

    @ApiOperation("当前对外生效的角标配置（无库表记录时返回内置默认值）")
    @GetMapping("/effective")
    public AjaxJson effective() {
        try {
            CreditScoreAvatarDisplayConfig config = creditScoreAvatarDisplayConfigService.getEffectiveCurrent();
            return AjaxJson.success().put("config", config);
        } catch (Exception e) {
            log.error("查询生效头像角标配置失败", e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("按用户解析头像信用分角标底色（依据注册时间与后台配置）")
    @GetMapping("/resolveBadge")
    public AjaxJson resolveBadge(@ApiParam(value = "用户id（t_member.id）", required = true) @RequestParam String userId) {
        try {
            if (StringUtils.isBlank(userId)) {
                return AjaxJson.error("userId不能为空");
            }
            CreditScoreAvatarBadgeVo badge = creditScoreAvatarDisplayConfigService.resolveBadgeForUser(userId.trim());
            return AjaxJson.success().put("badge", badge);
        } catch (Exception e) {
            log.error("解析头像角标失败, userId={}", userId, e);
            return AjaxJson.error("查询失败：" + e.getMessage());
        }
    }
}
