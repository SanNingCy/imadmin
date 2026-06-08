package com.web4x.integration.im;

import java.util.List;
import com.web4x.common.core.domain.entity.SysMenu;
import com.web4x.common.utils.StringUtils;


public final class SysMenuViewSupport
{
    private SysMenuViewSupport()
    {
    }

    public static List<SysMenu> normalizeForSidebar(List<SysMenu> menus)
    {
        if (menus == null || menus.isEmpty())
        {
            return menus;
        }
        for (SysMenu menu : menus)
        {
            normalizeNode(menu);
        }
        return menus;
    }

    private static void normalizeNode(SysMenu menu)
    {
        List<SysMenu> children = menu.getChildren();
        if (children != null && !children.isEmpty())
        {
            for (SysMenu child : children)
            {
                normalizeNode(child);
            }
            menu.setUrl("#");
            return;
        }
        if ("M".equals(menu.getMenuType()))
        {
            menu.setUrl("#");
            return;
        }
    }
}
