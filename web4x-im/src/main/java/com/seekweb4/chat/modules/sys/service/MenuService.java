package com.seekweb4.chat.modules.sys.service;

import com.seekweb4.chat.common.utils.CacheUtils;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.service.TreeService;
import com.seekweb4.chat.modules.sys.entity.DataRule;
import com.seekweb4.chat.modules.sys.entity.Menu;
import com.seekweb4.chat.modules.sys.mapper.MenuMapper;
import com.seekweb4.chat.modules.sys.utils.LogUtils;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 菜单.
 *
 * @author lixinapp
 * @version 2016-12-05
 */
@Service
@Transactional(readOnly = true)
public class MenuService extends TreeService<MenuMapper, Menu> {

    @Autowired
    private DataRuleService dataRuleService;

    public Menu get(String id) {
        return super.get(id);
    }

    public List<Menu> findAllMenu() {
        return UserUtils.getMenuList();
    }

    public List<Menu> getChildren(String parentId) {
        return super.getChildren(parentId);
    }

    @Transactional(readOnly = false)
    public void saveMenu(Menu menu) {
        // 获取修改前的parentIds，用于更新子节点的parentIds
        String oldParentIds = menu.getParentIds();
        
        // 如果是更新操作（有id）且前端没有传 parent 或 parentIds，从数据库加载完整的菜单信息
        if (menu.getId() != null && !menu.getId().isEmpty()) {
            Menu existingMenu = this.get(menu.getId());
            if (existingMenu != null) {
                // 如果 oldParentIds 为空，从数据库加载
                if (oldParentIds == null || oldParentIds.isEmpty()) {
                    oldParentIds = existingMenu.getParentIds();
                }
                // 如果前端没有传 parent 或 parent.id，使用数据库中的 parent，保持原有层级关系
                if (menu.getParent() == null || menu.getParent().getId() == null) {
                    menu.setParent(existingMenu.getParent());
                }
            }
        }
        
        // 获取父节点实体（修复空指针：确保 parent 不为 null 且有 id）
        if (menu.getParent() != null && menu.getParent().getId() != null) {
            menu.setParent(this.get(menu.getParent().getId()));
        } else {
            // 如果 parent 为 null，设置为根节点
            menu.setParent(this.get(Menu.getRootId()));
        }
        
        // 设置新的父节点串
        menu.setParentIds(menu.getParent().getParentIds() + menu.getParent().getId() + ",");
        // TODO 这个是根据:后面的权限标识去生成权限
        // 自动生成权限标识：如果 permission 只包含操作类型（如 list、add、edit、del），则根据 path 自动生成完整权限标识
        generatePermissionIfNeeded(menu);
        
        // 判断是否为新增操作（通过检查id是否为空或数据库中不存在）
        boolean isNewMenu = StringUtils.isBlank(menu.getId()) || this.get(menu.getId()) == null;
        
        // 保存或更新实体
        super.save(menu);
        
        // 如果是新增菜单，自动分配给所有角色
        if (isNewMenu) {
            mapper.assignMenuToAllRoles(menu.getId());
        }
        
        // 更新子节点 parentIds
        Menu m = new Menu();
        m.setParentIds("%," + menu.getId() + ",%");
        List<Menu> list = mapper.findByParentIdsLike(m);
        for (Menu e : list) {
            e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
            mapper.updateParentIds(e);
        }
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        UserUtils.removeCache(UserUtils.CACHE_TOP_MENU);
        // 清除日志相关缓存
        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    @Transactional(readOnly = false)
    public void updateSort(Menu menu) {
        mapper.updateSort(menu);
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        UserUtils.removeCache(UserUtils.CACHE_TOP_MENU);
        // 清除日志相关缓存
        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

//    @Transactional(readOnly = false)
//    public void delete(Menu menu) {
//        // 解除菜单角色关联
//        List<Map<String, Object>> mrlist = mapper.execSelectSql(
//                "SELECT distinct a.menu_id as id FROM sys_role_menu a left join sys_menu menu on a.menu_id = menu.id WHERE a.menu_id ='"
//                        + menu.getId() + "' OR menu.parent_ids LIKE  '%," + menu.getId() + ",%'");
//        for (Map<String, Object> mr : mrlist) {
//            mapper.deleteMenuRole(mr.get("id").toString());
//        }
//        // 删除菜单关联的数据权限数据，以及解除角色数据权限关联
//        List<Map<String, Object>> mdlist = mapper.execSelectSql(
//                "SELECT distinct a.id as id FROM sys_datarule a left join sys_menu menu on a.menu_id = menu.id WHERE a.menu_id ='"
//                        + menu.getId() + "' OR menu.parent_ids LIKE  '%," + menu.getId() + ",%'");
//        for (Map<String, Object> md : mdlist) {
//            DataRule dataRule = new DataRule(md.get("id").toString());
//            dataRuleService.delete(dataRule);
//        }
//
//        mapper.delete(menu);
//        // 清除用户菜单缓存
//        UserUtils.removeCache(UserUtils.CACHE_TOP_MENU);
//        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//        // 清除日志相关缓存
//        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
//    }



    @Transactional(readOnly = false)
    public void delete(Menu menu) {
        // 解除菜单角色关联
        List<Map<String, Object>> mrlist = mapper.execSelectSql(
                "SELECT distinct a.menu_id as id FROM sys_role_menu_two a left join sys_menu_two menu on a.menu_id = menu.id WHERE a.menu_id ='"
                        + menu.getId() + "' OR menu.parent_ids LIKE  '%," + menu.getId() + ",%'");
        for (Map<String, Object> mr : mrlist) {
            mapper.deleteMenuRole(mr.get("id").toString());
        }
        // 删除菜单关联的数据权限数据，以及解除角色数据权限关联
        List<Map<String, Object>> mdlist = mapper.execSelectSql(
                "SELECT distinct a.id as id FROM sys_datarule a left join sys_menu_two menu on a.menu_id = menu.id WHERE a.menu_id ='"
                        + menu.getId() + "' OR menu.parent_ids LIKE  '%," + menu.getId() + ",%'");
        for (Map<String, Object> md : mdlist) {
            DataRule dataRule = new DataRule(md.get("id").toString());
            dataRuleService.delete(dataRule);
        }

        mapper.delete(menu);
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_TOP_MENU);
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        // 清除日志相关缓存
        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    /**
     * 根据父菜单自动生成完整的权限标识
     * 如果permission只包含操作部分（如add、delete、view、edit），则根据父菜单的path或permission自动补全
     * 
     * @param menu 当前菜单
     * @param parentMenu 父菜单
     * @return 完整的权限标识，如 sys:menu:add
     */
    private String generateFullPermission(Menu menu, Menu parentMenu) {
        if (parentMenu == null || StringUtils.isBlank(menu.getPermission())) {
            return null;
        }
        
        String operation = menu.getPermission().trim(); // 操作部分，如 add、delete、view、edit
        
        // 查找最合适的父菜单（优先查找type="2"的菜单，因为它们通常有path）
        Menu targetMenu = findTargetMenuForPermission(parentMenu);
        if (targetMenu == null) {
            return null;
        }
        
        // 优先从目标菜单的permission中提取模块标识
        String modulePrefix = null;
        if (StringUtils.isNotBlank(targetMenu.getPermission()) && targetMenu.getPermission().contains(":")) {
            // 从 permission 中提取模块标识，如 sys:menu:list -> sys:menu
            String[] parts = targetMenu.getPermission().split(":");
            if (parts.length >= 2) {
                modulePrefix = parts[0] + ":" + parts[1];
            }
        }
        
        // 如果从permission中提取失败，则从path中提取
        if (StringUtils.isBlank(modulePrefix) && StringUtils.isNotBlank(targetMenu.getPath())) {
            modulePrefix = extractModuleFromPath(targetMenu.getPath());
        }
        
        // 如果找到了模块前缀，拼接成完整权限标识
        if (StringUtils.isNotBlank(modulePrefix)) {
            return modulePrefix + ":" + operation;
        }
        
        return null;
    }
    
    /**
     * 查找最适合用于生成权限标识的父菜单
     * 优先查找type="2"的菜单（页面菜单），因为它们通常有path或permission
     * 
     * @param parentMenu 父菜单
     * @return 目标菜单
     */
    private Menu findTargetMenuForPermission(Menu parentMenu) {
        if (parentMenu == null) {
            return null;
        }
        
        // 如果是根菜单，返回null
        if (Menu.getRootId().equals(parentMenu.getId())) {
            return null;
        }
        
        // 如果当前菜单是type="2"（页面菜单），且有path或permission，则使用它
        if ("2".equals(parentMenu.getType()) 
                && (StringUtils.isNotBlank(parentMenu.getPath()) 
                    || (StringUtils.isNotBlank(parentMenu.getPermission()) && parentMenu.getPermission().contains(":")))) {
            return parentMenu;
        }
        
        // 否则向上查找父菜单
        if (parentMenu.getParent() != null && StringUtils.isNotBlank(parentMenu.getParent().getId())) {
            Menu grandParent = this.get(parentMenu.getParent().getId());
            if (grandParent != null) {
                return findTargetMenuForPermission(grandParent);
            }
        }
        
        // 如果找不到合适的，返回当前菜单（至少尝试从它提取）
        return parentMenu;
    }
    
    /**
     * 从path中提取模块标识
     * 例如：/system/menu -> sys:menu, /system/role -> sys:role
     * 
     * @param path 路由路径
     * @return 模块标识，如 sys:menu
     */
    private String extractModuleFromPath(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        
        // 去掉开头的斜杠
        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        if (StringUtils.isBlank(cleanPath)) {
            return null;
        }
        
        // 按斜杠分割
        String[] parts = cleanPath.split("/");
        if (parts.length == 0) {
            return null;
        }
        
        // 将路径部分转换为模块标识
        // 例如：system -> sys, menu -> menu
        StringBuilder modulePrefix = new StringBuilder();
        for (int i = 0; i < parts.length && i < 2; i++) { // 最多取前两部分
            if (StringUtils.isNotBlank(parts[i])) {
                if (modulePrefix.length() > 0) {
                    modulePrefix.append(":");
                }
                // 特殊处理：system -> sys
                String part = parts[i].toLowerCase();
                if ("system".equals(part)) {
                    modulePrefix.append("system");
                } else {
                    modulePrefix.append(part);
                }
            }
        }
        
        return modulePrefix.length() > 0 ? modulePrefix.toString() : null;
    }

    /**
     * 如果需要，自动生成权限标识
     * 如果 permission 只包含操作类型（如 list、add、edit、del、view），则根据层级路径生成完整权限标识
     *
     * @param menu 菜单对象
     */
    private void generatePermissionIfNeeded(Menu menu) {
        // 如果 permission 为空，不需要生成
        if (StringUtils.isBlank(menu.getPermission())) {
            return;
        }

        // 如果已包含冒号，视为完整权限
        if (menu.getPermission().contains(":")) {
            return;
        }

        String modulePrefix = buildModulePrefix(menu);
        if (StringUtils.isBlank(modulePrefix)) {
            return;
        }

        String operation = menu.getPermission().trim();
        String fullPermission = modulePrefix + ":" + operation;
        menu.setPermission(fullPermission);
    }

    /**
     * 基于当前节点及祖先路径生成权限前缀，保留全部层级段，使用冒号连接
     * 处理规则：
     * - 从当前节点向上遍历到根，收集每个有 path 的菜单的 path 段
     * - system 段特殊处理为 sys，其余小写
     * - 顺序为顶层到当前（如 /ops -> /content -> /announcement -> 按钮 => ops:content:announcement）
     */
    private String buildModulePrefix(Menu menu) {
        List<String> segs = new ArrayList<>();
        Menu cur = menu;
        // 向上遍历，确保 parent 取到完整对象
        while (cur != null && cur.getParent() != null && cur.getParent().getId() != null) {
            if (StringUtils.isNotBlank(cur.getPath())) {
                addPathSegments(segs, cur.getPath());
            }
            // 继续向上，尽量用数据库中完整的父节点，防止 parent 缺失 path
            if (StringUtils.isNotBlank(cur.getParent().getId())) {
                cur = this.get(cur.getParent().getId());
            } else {
                cur = null;
            }
        }

        if (segs.isEmpty()) {
            return null;
        }
        Collections.reverse(segs);
        return StringUtils.join(segs, ":");
    }

    private void addPathSegments(List<String> segs, String path) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        String clean = path.startsWith("/") ? path.substring(1) : path;
        if (StringUtils.isBlank(clean)) {
            return;
        }
        String[] parts = clean.split("/");
        for (String p : parts) {
            if (StringUtils.isNotBlank(p)) {
                String part = p.toLowerCase();
                if ("system".equals(part)) {
                    part = "system";
                }
                segs.add(part);
            }
        }
    }

}
