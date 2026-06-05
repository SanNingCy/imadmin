package com.seekweb4.chat.api.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.seekweb4.chat.api.error.BizException;
import com.seekweb4.chat.common.utils.SpringContextHolder;
import com.seekweb4.chat.common.utils.StringRedisUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.config.web.Servlets;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.member.mapper.MemberMapper;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户工具类
 */
@Slf4j
public class MemberUtils {

    /**
     * 获取当前登录用户(必须带token)
     *
     * @return
     */
    public static Member getMember() {
        Member member = null;
        String uid = getUid();
        if (StringUtils.isNotBlank(uid)) {
            member = SpringContextHolder.getBean(MemberMapper.class).get(uid);
            if (member != null) {
                return member;
            }
            remove(uid);
        }
        throw new BizException("无效的token");
    }

    /**
     * 获取当前登录用户ID
     *
     * @return
     */
    public static String getUid() {
        return getUid(getToken());
    }

    /**
     * 获取当前登录用户ID
     * @param token 用户token
     * @return
     */
    public static String getUid(String token) {
        String uid = null;
        if (StringUtils.isNotBlank(token)) {
            try {
                DecodedJWT jwt = JWT.decode(token);
                uid = jwt.getClaim("uid").asString();
            } catch (Exception e) {

            }
        }
        log.debug("用户UID:{}", uid);
        return uid;
    }

    /**
     * 获取当前登录者token
     */
    public static String getToken() {
        return getToken(Servlets.getRequest());
    }

    public static String getToken(HttpServletRequest request) {
        try {
            String token = request.getHeader(JWTUtil.APPTOKEN);
            if(StringUtils.isNotBlank(token)){
                return token;
            }
            token = request.getParameter(JWTUtil.APPTOKEN);
            if(StringUtils.isNotBlank(token)){
                return token;
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 退出登录
     */
    public static void logout() {
        String token = getToken();
        StringRedisUtils.getInstance().delete(JWTUtil.APPTOKEN_CACHE + getUid(token) + ":" + token);
    }

    /**
     * 删除用户所有token
     * @param uid 用户ID
     */
    public static void remove(String uid) {
        StringRedisUtils redisUtils = StringRedisUtils.getInstance();
        Set<String> keys = redisUtils.keys(JWTUtil.APPTOKEN_CACHE + uid + "*");
        keys.forEach(key -> {
            String token = redisUtils.get(key);
            redisUtils.delete(JWTUtil.APPTOKEN_CACHE + uid + ":" + token);
        });
    }

    /**
     * 清空用户其余token（单点登录）
     * @param uid 用户ID
     */
    public static void ssoLogin(String uid) {
        StringRedisUtils redisUtils = StringRedisUtils.getInstance();
        Set<String> keys = redisUtils.keys(JWTUtil.APPTOKEN_CACHE + uid + "*");
        keys.forEach(key -> {
            redisUtils.setEx(key, "0", AppProperites.newInstance().getAPP_EXPIRE_TIME(), TimeUnit.MILLISECONDS);
        });
    }
}
