package com.seekweb4.chat.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seekweb4.chat.common.annotation.ApiLog;
import com.seekweb4.chat.common.json.AjaxJson;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.config.properties.AppProperites;
import com.seekweb4.chat.core.web.BaseController;
import com.seekweb4.chat.modules.sys.entity.Menu;
import com.seekweb4.chat.modules.sys.service.MenuService;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜单Controller
 *
 * @author lixinapp
 * @version 2016-3-23
 */

@RestController
@RequestMapping(value = "/sys/menu", produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    @ModelAttribute("menu")
    public Menu get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return menuService.get(id);
        } else {
            return new Menu();
        }
    }

    /**
     * 清除当前用户菜单相关缓存（菜单列表 + 顶层菜单）
     * 仅建议在菜单结构发生异常或迁移后手动调用一次
     */
    @ApiLog("清除当前用户菜单缓存")
//    @RequiresPermissions("sys:menu:list")
    @GetMapping("clearMenuCache")
    public AjaxJson clearMenuCache() {
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        UserUtils.removeCache(UserUtils.CACHE_TOP_MENU);
        return AjaxJson.success("已清除当前用户菜单缓存");
    }

    @ApiLog("查询菜单列表")
    @RequiresPermissions("system:menu:list")
//    @RequiresPermissions("sys:menu:list")
    @GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson list(Menu menu) {
        List<Menu> menus = Lists.newArrayList();
        List<Menu> allMenus = menuService.findAllMenu();
        String nameCondition = menu != null ? menu.getName() : null;
        
        // 过滤根菜单（ID为"1"），并根据name条件过滤
        for(Menu m : allMenus){
            if(!"1".equals(m.getId())){
                // 如果传了name参数，进行模糊匹配过滤
                if (StringUtils.isNotBlank(nameCondition)) {
                    if (m.getName() != null && m.getName().contains(nameCondition)) {
                        menus.add(m);
                    }
                } else {
                    // 没有name条件，保持原有逻辑，返回所有菜单
                    menus.add(m);
                }
            }
        }
        return AjaxJson.success().put("menuList", menus);
    }

    @ApiLog("查询菜单")
    @RequiresPermissions(value = {"system:menu:view", "system:menu:add", "system:menu:edit"}, logical = Logical.OR)
    @GetMapping("queryById")
    public AjaxJson queryById(Menu menu) {
        if (menu.getParent() == null || menu.getParent().getId() == null) {
            menu.setParent(new Menu(Menu.getRootId()));
        }
        menu.setParent(menuService.get(menu.getParent().getId()));
        return AjaxJson.success().put("menu", menu);
    }

    @ApiLog("保存菜单")
    @RequiresPermissions(value = {"system:menu:add", "system:menu:edit"}, logical = Logical.OR)
    @PostMapping(value = "save", produces = MediaType.APPLICATION_JSON_VALUE)
    public AjaxJson save(@RequestBody Menu menu) {
        if (!UserUtils.getUser().isAdmin()) {
            return AjaxJson.error("越权操作，只有超级管理员才能添加或修改数据！");
        }
        if (appProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作！");
        }
        /**
         * 后台hibernate-validation插件校验
         */
        String errMsg = beanValidator(menu);
        if (StringUtils.isNotBlank(errMsg)) {
            return AjaxJson.error(errMsg);
        }

        // 获取排序号，最末节点排序号+30
        if (StringUtils.isBlank(menu.getId())) {
            List<Menu> list = Lists.newArrayList();
            List<Menu> sourcelist = menuService.findAllMenu();
            Menu.sortList(list, sourcelist, menu.getParentId(), false);
            if (list.size() > 0) {
                menu.setSort(list.get(list.size() - 1).getSort() + 30);
            }
        }
        menuService.saveMenu(menu);

        return AjaxJson.success("保存成功!");
    }

    @ApiLog("删除菜单")
    @RequiresPermissions("system:menu:delete")
//    @RequiresPermissions("sys:menu:del")
    @DeleteMapping("delete")
    public AjaxJson delete(String ids) {
        AjaxJson j = new AjaxJson();
        if (appProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作");
        }
        String idArray[] =ids.split(",");
        for(String id : idArray){
            menuService.delete(new Menu (id));
        }
        return AjaxJson.success("删除成功!");
    }


    /**
     * 修改菜单排序
     */
    @ApiLog("修改菜单排序")
    @RequiresPermissions("sys:menu:updateSort")
    @PostMapping("sort")
    public AjaxJson sort(String id1, int sort1, String id2, int sort2) {
        AjaxJson j = new AjaxJson();
        if (appProperites.isDemoMode()) {
            return AjaxJson.error("演示模式，不允许操作！");
        }
        Menu menu = new Menu();
        menu.setId(id1);
        menu.setSort(sort1);
        menuService.updateSort(menu);
        menu.setId(id2);
        menu.setSort(sort2);
        menuService.updateSort(menu);
        return AjaxJson.success("排序成功！");
    }

    public List<Menu> getTreeMenu(List<Menu> list, String extId, String isShowHid) {
        Menu menu = menuService.get("1");
        List rootTree =  formatListToTree (menu, list, extId, isShowHid);
        return rootTree;
    }


    /**
     * 以root为根节点, 将allList从线性列表转为树形列表
     *
     * @param root 根节点, 为空抛出空指针异常
     * @param allList 所有需要参与构造为树的列表
     * @param extId 需要排除在树之外的节点(子节点一并被排除)
     * @return java.util.List<Menu>
     * @Author 滕鑫源
     * @Date 2020/10/23 17:04
     **/
    public List<Menu> formatListToTree(Menu root, List<Menu> allList, String extId, String isShowHide) {
        String rootId = root.getId();

        // 最终的树形态
        List<Menu> trees = Lists.newArrayList();

        // 把需要构造树的所有列表, 根据以父id作为key, 整理为列表
        Map<String, List<Menu>> treeMap = Maps.newHashMap();
        for (Menu menu : allList) {
            List<Menu> menus = treeMap.get(menu.getParent().getId());
            if (menus == null) {
                menus = Lists.newLinkedList();
            }

            // 剔除排除项, 构造treeMap, 转递归为线性操作
            if (StringUtils.isBlank(extId) ||  (!extId.equals(menu.getId()) && menu.getParentIds().indexOf("," + extId + ",") == -1)){
                if (isShowHide != null && isShowHide.equals(AppProperites.NO) && menu.getIsShow().equals(AppProperites.NO)) {
                    continue;
                }
                menus.add(menu);
                treeMap.put(menu.getParent().getId(), menus);
            }

        }

        // 没有给定的子树, 返回空树
        if (treeMap.get(rootId) == null || treeMap.get(rootId).isEmpty()) {
            return trees;
        }

        // 开始递归格式化
        List<Menu> children = treeMap.get(rootId);
        for (Menu parent : children) {
            formatFillChildren(parent, treeMap);
            trees.add(parent);
        }
        if (StringUtils.equals(rootId, "0")) {
            return children;
        } else {
            root.setChildren(trees);
            return Lists.newArrayList(root);
        }
    }

    /**
     * 从treeMap中取出子节点填入parent, 并递归此操作
     *
     * @param parent
     * @param treeMap
     * @return void
     * @Author 滕鑫源
     * @Date 2020/9/30 16:33
     **/
    private void formatFillChildren(Menu parent, Map<String, List<Menu>> treeMap) {
        List<Menu> children = treeMap.get(parent.getId());
        parent.setChildren(children);
        if (children != null && !children.isEmpty()) {
            for (Menu child : children) {
                formatFillChildren(child, treeMap);
            }
        }
    }
    /**
     * 显示的菜单包含功能菜单
     * isShowHide是否显示隐藏菜单
     *
     * @param extId
     * @return
     */
    @RequiresPermissions("user")
    @GetMapping("treeData")
    public AjaxJson treeData( @RequestParam(required = false) String extId, @RequestParam(required = false) String isShowHide) {
        List<Menu> list = menuService.findAllMenu();
        List rootTree = getTreeMenu(list, extId, isShowHide);
        return AjaxJson.success().put("treeData", rootTree);
    }

    /**
     * 不显示功能菜单
     *
     * @return
     */
    @RequiresPermissions("user")
    @GetMapping("treeData2")
    public AjaxJson treeData2(@RequestParam(required = false) String name) {
        List<Menu> list = menuService.findAllMenu();

        // 如果传了 name 条件，则按名称模糊过滤，并保留匹配菜单及其父级用于构建树
        if (StringUtils.isNotBlank(name)) {
            // 收集需要保留的菜单ID（匹配的菜单及其父级）
            java.util.Set<String> keepIds = new java.util.HashSet<>();
            for (Menu m : list) {
                if (m.getName() != null && m.getName().contains(name)) {
                    // 保留当前菜单
                    keepIds.add(m.getId());
                    // 保留所有父级ID
                    if (StringUtils.isNotBlank(m.getParentIds())) {
                        String[] parentIds = m.getParentIds().split(",");
                        for (String pid : parentIds) {
                            if (StringUtils.isNotBlank(pid)) {
                                keepIds.add(pid);
                            }
                        }
                    }
                }
            }
            // 如果没有任何匹配，直接返回空树
            if (keepIds.isEmpty()) {
                return AjaxJson.success().put("treeData", Lists.newArrayList());
            }
            // 根据保留的ID过滤菜单列表（始终保留根节点1，避免影响现有功能）
            List<Menu> filtered = Lists.newArrayList();
            for (Menu m : list) {
                if ("1".equals(m.getId()) || keepIds.contains(m.getId())) {
                    filtered.add(m);
                }
            }
            list = filtered;
        }

        List<Menu> rootTree = getTreeMenu(list, "", "");
        if (rootTree == null || rootTree.isEmpty() || rootTree.get(0).getChildren() == null) {
            return AjaxJson.success().put("treeData", Lists.newArrayList());
        }
        return AjaxJson.success().put("treeData", rootTree.get(0).getChildren());
    }

}
