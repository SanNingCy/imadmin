package com.web4x.integration.im;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import com.web4x.common.condition.ImShiroEnabledCondition;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.modules.sys.entity.Menu;
import com.seekweb4.chat.modules.sys.utils.MenuUtils;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import com.web4x.common.core.domain.entity.SysMenu;
import com.web4x.common.core.domain.entity.SysUser;

/**
 * 历史桥接：从 IM {@code sys_menu_two} 构建侧栏树。
 * 当前若依页面侧栏使用 {@code sys_menu_ry}，请用 {@link SysMenuViewSupport}。
 *
 * @deprecated 侧栏菜单已改读 {@code sys_menu_ry}，保留仅供 IM API 或其他调用方参考。
 */
@Deprecated
@Component
@Conditional(ImShiroEnabledCondition.class)
public class ImMenuTreeBridge
{
    private static final String ROOT_MENU_ID = "1";

    public List<SysMenu> selectMenusForUser(SysUser user)
    {
        List<Menu> imRoots = MenuUtils.getMenus();
        if (imRoots == null || imRoots.isEmpty())
        {
            return new ArrayList<>();
        }
        List<SysMenu> menus = new ArrayList<>();
        for (Menu imRoot : imRoots)
        {
            menus.add(convertTree(imRoot));
        }
        return menus;
    }

    /**
     * 与 {@code MenuController.formatListToTree} 一致：按 parent.id 分组，从根节点 1 展开。
     */
    static List<Menu> buildImMenuTree(List<Menu> allList)
    {
        Map<String, List<Menu>> treeMap = new LinkedHashMap<>();
        for (Menu menu : allList)
        {
            if (menu == null || !"1".equals(menu.getIsShow()) || "3".equals(menu.getType()))
            {
                continue;
            }
            String parentKey = resolveParentKey(menu);
            treeMap.computeIfAbsent(parentKey, k -> new LinkedList<>()).add(menu);
        }
        List<Menu> roots = treeMap.get(ROOT_MENU_ID);
        if (roots == null || roots.isEmpty())
        {
            return new ArrayList<>();
        }
        for (Menu parent : roots)
        {
            fillImChildren(parent, treeMap);
        }
        return roots;
    }

    private static String resolveParentKey(Menu menu)
    {
        if (menu.getParent() != null && StringUtils.isNotBlank(menu.getParent().getId()))
        {
            return menu.getParent().getId();
        }
        String parentId = menu.getParentId();
        return StringUtils.isNotBlank(parentId) ? parentId : "0";
    }

    private static void fillImChildren(Menu parent, Map<String, List<Menu>> treeMap)
    {
        List<Menu> children = treeMap.get(parent.getId());
        parent.setChildren(children == null ? new ArrayList<>() : children);
        for (Menu child : parent.getChildren())
        {
            fillImChildren(child, treeMap);
        }
    }

    private static SysMenu convertTree(Menu im)
    {
        SysMenu menu = toSysMenu(im);
        if (im.getChildren() != null && !im.getChildren().isEmpty())
        {
            List<SysMenu> children = new ArrayList<>();
            for (Menu child : im.getChildren())
            {
                children.add(convertTree(child));
            }
            menu.setChildren(children);
            menu.setUrl("#");
        }
        return menu;
    }

    static SysMenu toSysMenu(Menu im)
    {
        SysMenu menu = new SysMenu();
        if (NumberUtils.isCreatable(im.getId()))
        {
            menu.setMenuId(Long.parseLong(im.getId()));
        }
        long parentId = 0L;
        String imParentId = resolveParentKey(im);
        if (NumberUtils.isCreatable(imParentId))
        {
            parentId = Long.parseLong(imParentId);
        }
        menu.setParentId(parentId);
        menu.setMenuName(im.getName());
        menu.setUrl(StringUtils.defaultIfBlank(im.getPath(), "#"));
        menu.setTarget(im.getTarget());
        menu.setIcon(StringUtils.defaultIfBlank(im.getIcon(), "fa fa-folder-o"));
        menu.setPerms(im.getPermission());
        menu.setVisible(AppProperites.NO.equals(im.getIsShow()) ? "1" : "0");
        menu.setMenuType(mapMenuType(im.getType()));
        if (im.getSort() != null)
        {
            menu.setOrderNum(String.valueOf(im.getSort()));
        }
        if ("M".equals(menu.getMenuType()) && !"#".equals(menu.getUrl()))
        {
            menu.setUrl("#");
        }
        return menu;
    }

    /**
     * IM：1=目录，2=页面，3=按钮（已过滤）。
     * 若依：M=目录，C=菜单，F=按钮。
     */
    private static String mapMenuType(String imType)
    {
        if ("2".equals(imType))
        {
            return "C";
        }
        if ("3".equals(imType))
        {
            return "F";
        }
        return "M";
    }
}
