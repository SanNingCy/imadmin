package com.seekweb4.chat.modules.sys.security.shiro;

import com.seekweb4.chat.common.utils.CookieUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.web.GlobalErrorController;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends BasicHttpAuthenticationFilter {

    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Token字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = getToken(req);
        return authorization != null && !"null".equals(authorization)&& !"".equals(authorization);
    }

    /**
     *
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = getToken(httpServletRequest);

        JWTToken token = new JWTToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 获取token，支持三种方式, 请求参数、header、cookie， 优先级依次降低，以请求参数中的优先级最高。
     * @param httpServletRequest
     * @return
     */
    private String getToken(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader(JWTUtil.TOKEN);
        if(StringUtils.isNotBlank(token)){
            return token;
        }
        token = httpServletRequest.getParameter(JWTUtil.TOKEN);
        if(StringUtils.isNotBlank(token)){
            return token;
        }
        token = CookieUtils.getCookie(httpServletRequest, JWTUtil.TOKEN);
        if(StringUtils.isNotBlank(token)){
            return token;
        }
        return null;
    }


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                return executeLogin(request, response);
            } catch (AuthenticationException e) {
                GlobalErrorController.response401(request, response);//登录超时，需要刷新token
            }catch (Exception e){
                GlobalErrorController.response4021(request, response);//没有登录，需要登录
            }
        }else {
            GlobalErrorController.response4021(request, response);//没有登录，需要登录
        }

        return false;

    }

    /**
     * 对跨域提供支持
     * 统一处理 CORS，替代 nginx 的跨域配置
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // 获取请求的 Origin
        String origin = httpServletRequest.getHeader("X-Requested-Origin");
        if (origin == null || origin.isEmpty()) {
            origin = httpServletRequest.getHeader("Origin");
        }

        // 允许的源列表（Web端和管理后台）
        String[] allowedOrigins = {
                "https://ops.seekweb4.net",  // 管理后台
                "https://api.seekweb4.net"   // 移动端（如果是WebView或H5页面）
        };

        // 判断是否在允许的源列表中
        boolean isAllowedOrigin = false;
        String matchedOrigin = null;

        if (origin != null && !origin.isEmpty()) {
            for (String allowedOrigin : allowedOrigins) {
                if (allowedOrigin.equals(origin)) {
                    isAllowedOrigin = true;
                    matchedOrigin = origin;
                    break;
                }
            }
        }

        // 处理 OPTIONS 预检请求（必须在设置 CORS 头之前）
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            // 即使是预检请求，也需要验证 Origin
            if (isAllowedOrigin) {
                httpServletResponse.setHeader("Access-Control-Allow-Origin", matchedOrigin);
                httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH");

                String requestHeaders = httpServletRequest.getHeader("Access-Control-Request-Headers");
                if (requestHeaders == null || requestHeaders.trim().isEmpty()) {
                    requestHeaders = "Authorization,Content-Type,Accept,Origin,X-Requested-With,X-Requested-Origin,token,Token,DNT,User-Agent,Sec-CH-UA,Sec-CH-UA-Mobile,Sec-CH-UA-Platform";
                }
                httpServletResponse.setHeader("Access-Control-Allow-Headers", requestHeaders);
                httpServletResponse.setHeader("Access-Control-Max-Age", "1728000");
            }
            httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
            return false; // 不继续执行后续过滤器
        }

        // 设置 CORS 响应头（仅对跨域请求）
        if (isAllowedOrigin) {
            // 匹配到允许的源，设置 CORS 头
            httpServletResponse.setHeader("Access-Control-Allow-Origin", matchedOrigin);
            httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpServletResponse.setHeader("Vary", "Origin");
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH");

            // 处理 Access-Control-Request-Headers
            String requestHeaders = httpServletRequest.getHeader("Access-Control-Request-Headers");
            if (requestHeaders == null || requestHeaders.trim().isEmpty()) {
                // 默认允许的请求头
                requestHeaders = "Authorization,Content-Type,Accept,Origin,X-Requested-With,X-Requested-Origin,token,Token,DNT,User-Agent,Sec-CH-UA,Sec-CH-UA-Mobile,Sec-CH-UA-Platform";
            }
            httpServletResponse.setHeader("Access-Control-Allow-Headers", requestHeaders);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization,Set-Cookie,Content-Type");
        } else if (origin != null && !origin.isEmpty()) {
            // 不在允许列表中的源，根据业务需求处理
            // 这里默认不允许，如果需要允许可以添加到 allowedOrigins 列表
            // 或者设置为第一个允许的源
            httpServletResponse.setHeader("Access-Control-Allow-Origin", allowedOrigins[0]);
            httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpServletResponse.setHeader("Vary", "Origin");
        }
        // 如果没有 Origin 头（原生应用），不设置 CORS 头，因为原生应用不需要

        return super.preHandle(request, response);
    }


}
