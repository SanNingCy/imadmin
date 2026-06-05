package com.seekweb4.chat.api.utils.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 9:44
 */
@Aspect
@Component
public class RsaSignAspect {

    @Autowired
    private NonceCache nonceCache;

    @Autowired
    private KeyManager keyManager;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Pointcut("@annotation(RsaSignVerify)")
    public void verifyPoint() {
    }

    @Around("verifyPoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RsaSignVerify annotation = method.getAnnotation(RsaSignVerify.class);
        if (annotation == null) {
            return joinPoint.proceed();
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();

        ContentCachingRequestWrapper wrapper;
        if (request instanceof ContentCachingRequestWrapper) {
            wrapper = (ContentCachingRequestWrapper) request;
        } else {
            wrapper = new ContentCachingRequestWrapper(request, 1024 * 1024);
        }


        // 获取请求体完整 JSON
        String body = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(body)) {
            throw new SecurityException("请求体为空，无法验签");
        }

        Map<String, Object> map = MAPPER.readValue(body, Map.class);
        String appId = (String) map.get("appId");
        String sign = (String) map.get("sign");
        String nonce = (String) map.get("nonce");
        Long timestamp = map.get("timestamp") != null ? Long.parseLong(map.get("timestamp").toString()) : null;

        if (appId == null || sign == null) {
            throw new SecurityException("缺少 appId 或 sign");
        }

        //  校验时间戳（防过期）
        if (annotation.checkTimestamp()) {
            if (timestamp == null) {
                throw new SecurityException("缺少 timestamp");
            }
            long now = Instant.now().getEpochSecond();
            if (Math.abs(now - timestamp) > annotation.expireSeconds()) {
                throw new SecurityException("请求已过期或时间戳不合法");
            }
        }

        //   校验 nonce（防重放）
        if (annotation.checkNonce()) {
            if (StringUtils.isBlank(nonce)) {
                throw new SecurityException("缺少 nonce");
            }
            boolean first = nonceCache.checkAndAdd(nonce, annotation.expireSeconds());
            if (!first) {
                throw new SecurityException("重复请求，nonce 已使用");
            }
        }

        //  验签
        String signContent = buildSignContent(map);
        String pubKeyStr = keyManager.getPublicKey(appId);
        if (pubKeyStr == null) {
            throw new SecurityException("无效 appId");
        }

        PublicKey pubKey = RsaSignUtils.loadPublicKey(pubKeyStr);
        boolean valid = RsaSignUtils.verify(signContent, sign, pubKey);
        if (!valid) {
            throw new SecurityException("签名验证失败");
        }

        //  验签通过
        return joinPoint.proceed();
    }

    private String buildSignContent(Map<String, Object> params) {
        Map<String, Object> sorted = new TreeMap<>();
        for (Map.Entry<String, Object> e : params.entrySet()) {
            if (!"sign".equals(e.getKey()) && e.getValue() != null) {
                sorted.put(e.getKey(), e.getValue());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : sorted.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(e.getKey()).append("=").append(e.getValue());
        }
        return sb.toString();
    }

}