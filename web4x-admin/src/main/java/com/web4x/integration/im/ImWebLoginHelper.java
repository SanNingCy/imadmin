package com.web4x.integration.im;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.seekweb4.chat.common.utils.CookieUtils;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import com.seekweb4.chat.modules.sys.utils.UserUtils;

/**
 * 若依表单登录成功后写入 IM JWT Cookie，供 {@code JWTFilter} 鉴权后续页面请求。
 */
@Component
@ConditionalOnProperty(name = "im.shiro.enabled", havingValue = "true", matchIfMissing = true)
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
}
