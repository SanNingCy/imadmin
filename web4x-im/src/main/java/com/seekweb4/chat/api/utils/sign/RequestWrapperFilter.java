package com.seekweb4.chat.api.utils.sign;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 11:45
 */
@Component
public class RequestWrapperFilter implements Filter {

    /** Spring 6+ 构造器要求指定缓存上限（字节） */
    private static final int REQUEST_CACHE_LIMIT = 1024 * 1024;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, REQUEST_CACHE_LIMIT);
        chain.doFilter(wrappedRequest, resp);
    }
}
