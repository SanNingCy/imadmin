package com.web4x.integration.im;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.modules.sys.entity.Menu;
import com.seekweb4.chat.modules.sys.utils.MenuUtils;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import com.web4x.common.core.domain.entity.SysMenu;
import com.web4x.common.core.domain.entity.SysUser;

/**
 * 若依首页侧栏菜单：与 IM 后台相同树形（{@link MenuUtils#getMenus()} / treeData2）。
 */
@Component
@ConditionalOnProperty(name = "im.shiro.enabled", havingValue = "true", matchIfMissing = true)
public class ImMenuTreeBridge
{
    private static final String ROOT_MENU_ID = "1";

    public List<SysMenu> selectMenusForUser(SysUser user)
    {
        List<Menu> imRoots = buildImMenuTree(UserUtils.getMenuList());
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
            if (StringUtils.isBlank(im.getPath()))
            {
                menu.setUrl("#");
            }
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
        String url = StringUtils.defaultIfBlank(im.getPath(), "#");
        menu.setUrl(url);
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
