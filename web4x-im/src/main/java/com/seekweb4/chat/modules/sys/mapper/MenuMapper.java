package com.seekweb4.chat.modules.sys.mapper;

import java.util.List;

import com.seekweb4.chat.core.persistence.TreeMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.seekweb4.chat.modules.sys.entity.Menu;

/**
 * 菜单MAPPER接口
 * @author lixinapp
 * @version 2017-05-16
 */
@Mapper
public interface MenuMapper extends TreeMapper<Menu> {

	public List<Menu> findByUserId(Menu menu);

	public void deleteMenuRole(@Param("menu_id") String menu_id);

	public void deleteMenuDataRule(@Param("menu_id") String menu_id);

	public List<Menu> findAllDataRuleList(Menu menu);

	/**
	 * 将菜单分配给所有角色
	 * @param menuId 菜单ID
	 */
	public void assignMenuToAllRoles(@Param("menu_id") String menuId);

}
