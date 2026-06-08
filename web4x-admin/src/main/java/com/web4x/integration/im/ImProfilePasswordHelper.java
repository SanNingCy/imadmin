package com.web4x.integration.im;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import com.seekweb4.chat.common.utils.CookieUtils;
import com.seekweb4.chat.modules.sys.entity.User;
import com.seekweb4.chat.modules.sys.security.shiro.JWTToken;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import com.seekweb4.chat.modules.sys.service.UserService;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import com.web4x.common.condition.ImShiroEnabledCondition;

/**
 * IM 模式下个人信息改密：校验/更新 {@code sys_user}，并刷新 JWT 会话。
 */
@Component
@Conditional(ImShiroEnabledCondition.class)
public class ImProfilePasswordHelper
{
    private static final Logger log = LoggerFactory.getLogger(ImProfilePasswordHelper.class);

    private final UserService userService;

    public ImProfilePasswordHelper(UserService userService)
    {
        this.userService = userService;
    }

    /** 校验登录名与明文密码是否匹配（与 IM 登录规则一致）。 */
    public boolean matchesPassword(String loginName, String rawPassword)
    {
        User user = UserUtils.getByLoginName(loginName);
        return user != null && UserService.validatePassword(rawPassword, user.getPassword());
    }

    /** 修改当前用户密码并刷新 JWT Cookie。 */
    public void changePassword(String loginName, String newPassword, HttpServletResponse response)
    {
        User user = UserUtils.getByLoginName(loginName);
        if (user == null || org.apache.commons.lang3.StringUtils.isBlank(user.getId()))
        {
            throw new AuthenticationException("用户不存在或已失效，请重新登录");
        }
        userService.updatePasswordById(user.getId(), loginName, newPassword);
        refreshSessionToken(loginName, response);
    }

    /**
     * 管理员重置密码时，同步更新 IM {@code sys_user} 表及 Redis 用户缓存。
     */
    public void syncPasswordByLoginName(String loginName, String plainPassword)
    {
        User user = UserUtils.getByLoginName(loginName);
        if (user == null || org.apache.commons.lang3.StringUtils.isBlank(user.getId()))
        {
            return;
        }
        userService.updatePasswordById(user.getId(), loginName, plainPassword);
    }

    /** 改密后重新签发 JWT 并写入 Cookie。 */
    private void refreshSessionToken(String loginName, HttpServletResponse response)
    {
        User user = UserUtils.reloadByLoginName(loginName);
        if (user == null || org.apache.commons.lang3.StringUtils.isBlank(user.getPassword()))
        {
            return;
        }
        String token = JWTUtil.createAccessToken(loginName, user.getPassword());
        try
        {
            SecurityUtils.getSubject().login(new JWTToken(token));
            CookieUtils.setCookie(response, JWTUtil.TOKEN, token);
        }
        catch (Exception e)
        {
            log.warn("改密后刷新 JWT 失败，请重新登录: {}", e.getMessage());
        }
    }
}
