package com.seekweb4.chat.modules.sys.utils;

import com.google.common.collect.Lists;
import com.seekweb4.chat.modules.sys.entity.Menu;

import java.util.List;

public class MenuUtils {

    public static List<Menu> getMenus() {
        Menu menu = UserUtils.getTopMenu();
        getChildOfTree(menu, 0, UserUtils.getMenuList());
        return menu.getChildren();
    }

    private static Menu getChildOfTree(Menu menuItem, int level, List<Menu> menuList) {
        if (menuItem.getIsShow().equals("1")) {// 如果是父节点且显示
            menuItem.setChildren(Lists.newArrayList());
            for (Menu child : menuList) {
                if (!child.getType().equals("3") && child.getParentId().equals(menuItem.getId()) && child.getIsShow().equals("1")) {
                    menuItem.getChildren().add(getChildOfTree(child, level + 1, menuList));
                }
            }
        }
        return menuItem;
    }

}
