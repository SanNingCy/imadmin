package com.web4x.integration.im;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import com.web4x.common.condition.ImShiroEnabledCondition;
import jakarta.annotation.PostConstruct;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.modules.sys.entity.User;
import com.seekweb4.chat.modules.sys.security.util.JWTUtil;
import com.seekweb4.chat.modules.sys.service.UserService;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import com.web4x.common.core.domain.entity.SysUser;
import com.web4x.common.shiro.SysUserPrincipalBridge;
import com.web4x.common.utils.ShiroUtils;

/**
 * IM 用户 → 若依 {@link SysUser}，供 Thymeleaf 与 ShiroUtils 使用。
 */
@Component
@Conditional(ImShiroEnabledCondition.class)
public class ImSysUserPrincipalBridge implements SysUserPrincipalBridge
{
    @PostConstruct
    void register()
    {
        ShiroUtils.registerPrincipalBridge(this);
    }

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public SysUser resolve(Object principal)
    {
        if (principal == null)
        {
            return null;
        }
        String loginName = JWTUtil.getLoginName(principal.toString());
        if (StringUtils.isBlank(loginName))
        {
            return null;
        }
        User imUser = UserUtils.getByLoginName(loginName);
        if (imUser == null || StringUtils.isBlank(imUser.getId()))
        {
            return null;
        }
        return toSysUser(imUser);
    }

    @Override
    public boolean validatePassword(String loginName, String rawPassword)
    {
        User user = UserUtils.getByLoginName(loginName);
        return user != null && UserService.validatePassword(rawPassword, user.getPassword());
    }

    static SysUser toSysUser(User im)
    {
        SysUser sys = new SysUser();
        if (NumberUtils.isCreatable(im.getId()))
        {
            sys.setUserId(Long.parseLong(im.getId()));
        }
        sys.setLoginName(im.getLoginName());
        sys.setUserName(im.getName());
        sys.setEmail(im.getEmail());
        sys.setPhonenumber(im.getMobile());
        sys.setAvatar(im.getPhoto());
        sys.setLoginIp(im.getLoginIp());
        sys.setLoginDate(im.getLoginDate());
        sys.setStatus(AppProperites.NO.equals(im.getLoginFlag()) ? "1" : "0");
        return sys;
    }
}
