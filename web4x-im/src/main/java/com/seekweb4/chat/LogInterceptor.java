//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.seekweb4.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.DateUtils;
import com.seekweb4.chat.modules.sys.utils.LogUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LogInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);
    private static final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal("ThreadLocal StartTime");

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (NetUtil.handle(request)) {
                this.doResponse(response, AjaxJson.error(""));
                return false;
            }
        } catch (Exception var6) {
        }

        if (log.isDebugEnabled()) {
            long beginTime = System.currentTimeMillis();
            startTimeThreadLocal.set(beginTime);
            log.debug("开始计时: {}  URI: {}", (new SimpleDateFormat("hh:mm:ss.SSS")).format(beginTime), request.getRequestURI());
        }

        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            log.info("ViewName: " + modelAndView.getViewName());
        }

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (!StringUtils.startsWithIgnoreCase(request.getRequestURI(), "/api")) {
            LogUtils.saveLog(request, handler, ex, (String)null, (String)null);
        }

        if (log.isDebugEnabled()) {
            long beginTime = (Long)startTimeThreadLocal.get();
            long endTime = System.currentTimeMillis();
            log.debug("计时结束：{}  耗时：{}  URI: {}  最大内存: {}m  已分配内存: {}m  已分配内存中的剩余空间: {}m  最大可用内存: {}m", new Object[]{(new SimpleDateFormat("hh:mm:ss.SSS")).format(endTime), DateUtils.formatDateTime(endTime - beginTime), request.getRequestURI(), Runtime.getRuntime().maxMemory() / 1024L / 1024L, Runtime.getRuntime().totalMemory() / 1024L / 1024L, Runtime.getRuntime().freeMemory() / 1024L / 1024L, (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1024L / 1024L});
        }

    }

    private void doResponse(HttpServletResponse response, AjaxJson json) throws IOException {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            PrintWriter out = response.getWriter();
            String s = (new ObjectMapper()).writeValueAsString(json);
            out.print(s);
            out.flush();
            out.close();
        } catch (Throwable ex) {
            throw ex;
        }
    }
}
