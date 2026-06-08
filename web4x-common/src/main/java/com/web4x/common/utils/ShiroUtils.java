package com.web4x.common.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import com.web4x.common.core.domain.entity.SysUser;
import com.web4x.common.shiro.SysUserPrincipalBridge;

/**
 * shiro 工具类
 * 
 * @author web4x
 */
public class ShiroUtils
{
    private static volatile SysUserPrincipalBridge principalBridge;

    public static void registerPrincipalBridge(SysUserPrincipalBridge bridge)
    {
        principalBridge = bridge;
    }
    public static Subject getSubject()
    {
        return SecurityUtils.getSubject();
    }

    public static Session getSession()
    {
        return SecurityUtils.getSubject().getSession();
    }

    public static void logout()
    {
        getSubject().logout();
    }

    public static SysUser getSysUser()
    {
        Object obj = getSubject().getPrincipal();
        if (StringUtils.isNull(obj))
        {
            return null;
        }
        if (obj instanceof SysUser)
        {
            return (SysUser) obj;
        }
        if (principalBridge != null && principalBridge.isActive())
        {
            return principalBridge.resolve(obj);
        }
        return null;
    }

    public static void setSysUser(SysUser user)
    {
        Subject subject = getSubject();
        releaseRunAs();
        if (principalBridge != null && principalBridge.isActive())
        {
            principalBridge.onSessionUserUpdated(user);
            return;
        }
        PrincipalCollection principalCollection = subject.getPrincipals();
        String realmName = principalCollection.getRealmNames().iterator().next();
        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(user, realmName);
        subject.runAs(newPrincipalCollection);
    }

    /** IM 模式下 runAs 会把 JWT 换成 SysUser，导致 kickout 等过滤器 ClassCastException */
    public static void releaseRunAs()
    {
        Subject subject = getSubject();
        if (subject != null && subject.isRunAs())
        {
            subject.releaseRunAs();
        }
    }

    public static Long getUserId()
    {
        return getSysUser().getUserId().longValue();
    }

    public static String getLoginName()
    {
        return getSysUser().getLoginName();
    }

    public static String getIp()
    {
        return StringUtils.substring(getSubject().getSession().getHost(), 0, 128);
    }

    public static String getSessionId()
    {
        return String.valueOf(getSubject().getSession().getId());
    }

    /**
     * 是否为管理员
     * 
     * @return 结果
     */
    public static boolean isAdmin()
    {
        return isAdmin(getUserId());
    }

    /**
     * 是否为管理员
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    /**
     * 生成随机盐
     */
    public static String randomSalt()
    {
        // 一个Byte占两个字节，此处生成的3字节，字符串长度为6
        SecureRandomNumberGenerator secureRandom = new SecureRandomNumberGenerator();
        String hex = secureRandom.nextBytes(3).toHex();
        return hex;
    }
}
