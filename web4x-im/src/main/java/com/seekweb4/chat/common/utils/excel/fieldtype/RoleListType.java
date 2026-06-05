package com.seekweb4.chat.common.utils.excel.fieldtype;

import com.google.common.collect.Lists;
import com.seekweb4.chat.common.utils.Collections3;
import com.seekweb4.chat.common.utils.SpringContextHolder;
import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.modules.sys.entity.Role;
import com.seekweb4.chat.modules.sys.service.RoleService;

import java.util.List;

/**
 * 字段类型转换
 * @author lixinapp
 * @version 2016-5-29
 */
public class RoleListType {


	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		List<Role> roleList = Lists.newArrayList();
		List<Role> allRoleList = SpringContextHolder.getBean(RoleService.class).findAllRole();
		for (String s : StringUtils.split(val, ",")){
			for (Role e : allRoleList){
				if (StringUtils.trimToEmpty(s).equals(e.getName())){
					roleList.add(e);
				}
			}
		}
		return roleList.size()>0?roleList:null;
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null){
			@SuppressWarnings("unchecked")
			List<Role> roleList = (List<Role>)val;
			return Collections3.extractToString(roleList, "name", ", ");
		}
		return "";
	}

}
