package com.web4x.common.shiro;

import com.web4x.common.core.domain.entity.SysUser;

/**
 * IM Shiro 启用时，将 JWT 会话主体解析为若依 {@link SysUser}。
 */
public interface SysUserPrincipalBridge
{
    boolean isActive();

    SysUser resolve(Object principal);

    boolean validatePassword(String loginName, String rawPassword);

    /** IM 模式下会话 principal 保持 JWT，仅刷新用户缓存 */
    default void onSessionUserUpdated(SysUser user)
    {
    }
}
