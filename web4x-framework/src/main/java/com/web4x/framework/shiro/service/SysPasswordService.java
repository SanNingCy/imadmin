package com.web4x.framework.shiro.service;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.web4x.common.constant.Constants;
import com.web4x.common.constant.ShiroConstants;
import com.web4x.common.core.domain.entity.SysUser;
import com.web4x.common.exception.user.UserPasswordNotMatchException;
import com.web4x.common.exception.user.UserPasswordRetryLimitExceedException;
import com.web4x.common.utils.MessageUtils;
import com.web4x.common.utils.StringUtils;
import com.web4x.common.utils.security.Md5Utils;
import com.web4x.framework.manager.AsyncManager;
import com.web4x.framework.manager.factory.AsyncFactory;
import com.web4x.system.service.ISysUserService;
import jakarta.annotation.PostConstruct;

/**
 * 登录密码方法
 * 
 * @author web4x
 */
@Component
public class SysPasswordService
{
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ISysUserService userService;

    private Cache<String, AtomicInteger> loginRecordCache;

    @Value(value = "${user.password.maxRetryCount:5}")
    private String maxRetryCount;

    @PostConstruct
    public void init()
    {
        loginRecordCache = cacheManager.getCache(ShiroConstants.LOGIN_RECORD_CACHE);
    }

    public void validate(SysUser user, String password)
    {
        String loginName = user.getLoginName();

        AtomicInteger retryCount = loginRecordCache.get(loginName);

        if (retryCount == null)
        {
            retryCount = new AtomicInteger(0);
            loginRecordCache.put(loginName, retryCount);
        }
        if (retryCount.incrementAndGet() > Integer.valueOf(maxRetryCount).intValue())
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.exceed", maxRetryCount)));
            throw new UserPasswordRetryLimitExceedException(Integer.valueOf(maxRetryCount).intValue());
        }

        if (!matches(user, password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.count", retryCount)));
            loginRecordCache.put(loginName, retryCount);
            throw new UserPasswordNotMatchException();
        }
        else
        {
            clearLoginRecordCache(loginName);
        }
    }

    public boolean matches(SysUser user, String newPassword)
    {
        if (user == null || StringUtils.isEmpty(newPassword))
        {
            return false;
        }
        String loginName = user.getLoginName();
        String storedPassword = user.getPassword();
        String salt = user.getSalt();
        if (StringUtils.isEmpty(storedPassword))
        {
            SysUser dbUser = userService.selectUserByLoginName(loginName);
            if (dbUser == null)
            {
                return false;
            }
            storedPassword = dbUser.getPassword();
            salt = dbUser.getSalt();
        }
        if (StringUtils.isEmpty(storedPassword))
        {
            return false;
        }
        return StringUtils.equals(storedPassword, encryptPassword(loginName, newPassword, salt));
    }

    public void clearLoginRecordCache(String loginName)
    {
        loginRecordCache.remove(loginName);
    }

    public String encryptPassword(String loginName, String password, String salt)
    {
        return Md5Utils.hash(loginName + password + salt);
    }
}
