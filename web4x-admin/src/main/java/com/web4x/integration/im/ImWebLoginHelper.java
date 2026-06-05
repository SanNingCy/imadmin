package com.web4x.integration.im;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.subject.Subject;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import com.web4x.common.condition.ImShiroEnabledCondition;
import com.seekweb4.chat.common.utils.CookieUtils;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import com.seekweb4.chat.modules.sys.utils.UserUtils;

/**
 * 若依表单登录成功后写入 IM JWT Cookie，供 {@code JWTFilter} 鉴权后续页面请求。
 */
@Component
@Conditional(ImShiroEnabledCondition.class)
public class ImWebLoginHelper
{
    public void writeTokenCookie(HttpServletResponse response)
    {
        String token = UserUtils.getToken();
        if (token != null)
        {
            CookieUtils.setCookie(response, JWTUtil.TOKEN, token);
        }
    }

    public void clearTokenCookie(HttpServletResponse response)
    {
        CookieUtils.setCookie(response, JWTUtil.TOKEN, "", 0);
    }

    /**
     * 完整退出：清理 IM 用户缓存、注销 Shiro 会话并清除 JWT Cookie。
     */
    public void logout(HttpServletResponse response)
    {
        try
        {
            Subject subject = UserUtils.getSubject();
            if (subject != null && subject.getPrincipal() != null)
            {
                try
                {
                    UserUtils.clearCache();
                }
                catch (Exception ignored)
                {
                }
                subject.logout();
            }
        }
        catch (Exception ignored)
        {
        }
        clearTokenCookie(response);
    }
}
