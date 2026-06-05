package com.seekweb4.chat.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekweb4.chat.api.utils.MemberUtils;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import lombok.SneakyThrows;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * 移动端JWT TOKEN拦截器
 */
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())){
            return true;
        }
        AppIntercept intercept = ((HandlerMethod) handler).getMethodAnnotation(AppIntercept.class);
        if (intercept != null) {
            return true;
        }
        String token = MemberUtils.getToken(request);
        if (StringUtils.isBlank(token)) {
            doResponse(response, AjaxJson.fail("请先登录"));
            return false;
        }
        String uid = MemberUtils.getUid(token);
        if (StringUtils.isBlank(uid)) {
            doResponse(response, AjaxJson.fail("token无效"));
            return false;
        }
        StringRedisUtils redisUtils = StringRedisUtils.getInstance();
        String key = JWTUtil.APPTOKEN_CACHE + uid + ":" + token;
        String apptoken = redisUtils.get(key);
        if (StringUtils.isBlank(apptoken)) {
            doResponse(response, AjaxJson.fail("您的登录已过期，请重新登录！"));
            return false;
        }
        if ("0".equals(apptoken)) {// 单点登录
            redisUtils.delete(key);
            doResponse(response, AjaxJson.fail("您的账号在另一台设备上登录，如非本人操作，请立即修改密码！"));
            return false;
        }
        redisUtils.expire(key, AppProperites.newInstance().getAPP_EXPIRE_TIME(), TimeUnit.MILLISECONDS);
        return true;
    }

    @SneakyThrows
    private void doResponse(HttpServletResponse response, AjaxJson res) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String s = new ObjectMapper().writeValueAsString(res);
        //log.debug("TOKEN校验失败: {}", s);
        out.print(s);
        out.flush();
        out.close();
    }
}
