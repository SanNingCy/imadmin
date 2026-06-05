package com.web4x.framework.web.service;

import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.web4x.common.core.redis.RedisUtils;

/**
 * 验证码Redis存取服务。
 *
 * @author web4x
 */
@Service
public class CaptchaService
{
    @Value("${captcha.expireTime:2}")
    private int expireTime;

    public void setCode(HttpServletRequest request, String code)
    {
        RedisUtils.set(getCaptchaKey(request), code, expireTime, TimeUnit.MINUTES);
    }

    public String getCode(HttpServletRequest request)
    {
        Object code = RedisUtils.get(getCaptchaKey(request));
        return code == null ? "" : String.valueOf(code);
    }

    public void removeCode(HttpServletRequest request)
    {
        RedisUtils.delete(getCaptchaKey(request));
    }

    private String getCaptchaKey(HttpServletRequest request)
    {
        return "web4x:captcha:" + request.getSession().getId();
    }
}
