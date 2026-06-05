package com.web4x.integration.im;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.seekweb4.chat.modules.sys.entity.Menu;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import com.web4x.common.core.domain.entity.SysMenu;
import com.web4x.common.core.domain.entity.SysUser;

/**
 * 若依首页侧栏菜单：数据来自 IM {@code sys_menu_two}（{@link UserUtils#getMenuList()}）。
 */
@Component
@ConditionalOnProperty(name = "im.shiro.enabled", havingValue = "true", matchIfMissing = true)
public class ImMenuTreeBridge
{
    public List<SysMenu> selectMenusForUser(SysUser user)
    {
        List<Menu> imMenus = UserUtils.getMenuList();
        List<SysMenu> flat = new ArrayList<>();
        for (Menu menu : imMenus)
        {
            if (menu == null || !"1".equals(menu.getIsShow()))
            {
                continue;
            }
            if ("3".equals(menu.getType()))
            {
                continue;
            }
            flat.add(toSysMenu(menu));
        }
        long rootParentId = resolveRootParentId(flat);
        return buildTree(flat, rootParentId);
    }

    private static long resolveRootParentId(List<SysMenu> flat)
    {
        for (SysMenu m : flat)
        {
            if (m.getParentId() != null && (m.getParentId() == 0L || m.getParentId() == 1L))
            {
                return m.getParentId();
            }
        }
        return 0L;
    }

    private static List<SysMenu> buildTree(List<SysMenu> list, long parentId)
    {
        List<SysMenu> roots = new LinkedList<>();
        for (SysMenu item : list)
        {
            if (item.getParentId() != null && item.getParentId() == parentId)
            {
                attachChildren(list, item);
                roots.add(item);
            }
        }
        if (roots.isEmpty() && parentId == 0L)
        {
            for (SysMenu item : list)
            {
                if (item.getParentId() != null && item.getParentId() == 1L)
                {
                    attachChildren(list, item);
                    roots.add(item);
                }
            }
        }
        return roots;
    }

    private static void attachChildren(List<SysMenu> list, SysMenu parent)
    {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu n : list)
        {
            if (n.getParentId() != null && n.getParentId().equals(parent.getMenuId()))
            {
                attachChildren(list, n);
                children.add(n);
            }
        }
        parent.setChildren(children);
    }

    static SysMenu toSysMenu(Menu im)
    {
        SysMenu menu = new SysMenu();
        if (NumberUtils.isCreatable(im.getId()))
        {
            menu.setMenuId(Long.parseLong(im.getId()));
        }
        long parentId = 0L;
        if (StringUtils.isNotBlank(im.getParentId()) && NumberUtils.isCreatable(im.getParentId()))
        {
            parentId = Long.parseLong(im.getParentId());
            if (parentId == 1L)
            {
                parentId = 0L;
            }
        }
        menu.setParentId(parentId);
        menu.setMenuName(im.getName());
        menu.setUrl(StringUtils.defaultIfBlank(im.getPath(), "#"));
        menu.setTarget(im.getTarget());
        menu.setIcon(im.getIcon());
        menu.setPerms(im.getPermission());
        menu.setVisible("1".equals(im.getIsShow()) ? "0" : "1");
        menu.setMenuType(mapMenuType(im.getType()));
        if (im.getSort() != null)
        {
            menu.setOrderNum(String.valueOf(im.getSort()));
        }
        return menu;
    }

    private static String mapMenuType(String imType)
    {
        if ("1".equals(imType))
        {
            return "C";
        }
        if ("2".equals(imType))
        {
            return "M";
        }
        return "M";
    }
}
