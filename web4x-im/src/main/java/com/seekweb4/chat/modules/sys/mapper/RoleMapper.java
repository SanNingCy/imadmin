package com.seekweb4.chat.modules.sys.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.sys.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色MAPPER接口
 * @author lixinapp
 * @version 2016-12-05
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

	public Role getByName(Role role);

	public Role getByEnname(Role role);

	/**
	 * 查询角色的所有无下属菜单ID
	 * @param id
	 * @return
	 */
	public List<String> queryAllNotChildrenMenuId(String id);
	/**
	 * 维护角色与菜单权限关系
	 * @param role
	 * @return
	 */
	public int deleteRoleMenu(Role role);

	public int insertRoleMenu(Role role);

	/**
	 * 维护角色与数据权限关系
	 * @param role
	 * @return
	 */
	public int deleteRoleDataRule(Role role);

	public int insertRoleDataRule(Role role);

}
