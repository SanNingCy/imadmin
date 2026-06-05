package com.seekweb4.chat.modules.sys.service;

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.sys.entity.Role;
import com.seekweb4.chat.modules.sys.entity.User;
import com.seekweb4.chat.modules.sys.mapper.RoleMapper;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色管理类
 *
 * @author lixinapp
 * @version 2016-12-05
 */
@Service
@Transactional(readOnly = true)
public class RoleService extends CrudService<RoleMapper, Role> {

    @Autowired
    private UserService userService;

    public Role get(String id) {
        return mapper.get(id);
    }

    public Role getRoleByName(String name) {
        Role r = new Role();
        r.setName(name);
        return mapper.getByName(r);
    }

    public Role getRoleByEnname(String enname) {
        Role r = new Role();
        r.setEnname(enname);
        return mapper.getByEnname(r);
    }

    /**
     * 查询角色的所有无下属菜单ID
     * @param id
     * @return
     */
    public List<String> queryAllNotChildrenMenuId(String id){
        if(StringUtils.isNotEmpty(id)){
            return mapper.queryAllNotChildrenMenuId(id);
        }
        return Lists.newArrayList();
    }
    public List<Role> findAllRole() {
        return UserUtils.getRoleList();
    }

    @Transactional(readOnly = false)
    public void saveRole(Role role) {
        if (StringUtils.isBlank(role.getId())) {
            role.preInsert();
            mapper.insert(role);
        } else {
            role.preUpdate();
            mapper.update(role);
        }
        // 更新角色与菜单关联
        mapper.deleteRoleMenu(role);
        if (role.getMenuList().size() > 0) {
            mapper.insertRoleMenu(role);
        }

        // 更新角色与数据权限关联
        mapper.deleteRoleDataRule(role);
        if (role.getDataRuleList().size() > 0) {
            mapper.insertRoleDataRule(role);
        }
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
    }

    @Transactional(readOnly = false)
    public void deleteRole(Role role) {
        mapper.deleteRoleMenu(role);
        mapper.deleteRoleDataRule(role);
        mapper.delete(role);
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
    }

    @Transactional(readOnly = false)
    public Boolean outUserInRole(Role role, User user) {
        List<Role> roles = user.getRoleList();
        for (Role e : roles) {
            if (e.getId().equals(role.getId())) {
                roles.remove(e);
                userService.saveUser(user);
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = false)
    public User assignUserToRole(Role role, User user) {
        if (user == null) {
            return null;
        }
        List<String> roleIds = user.getRoleIdList();
        if (roleIds.contains(role.getId())) {
            return null;
        }
        user.getRoleList().add(role);
        userService.saveUser(user);
        return user;
    }


}
