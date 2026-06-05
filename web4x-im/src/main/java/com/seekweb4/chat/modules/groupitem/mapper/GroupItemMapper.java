package com.seekweb4.chat.modules.groupitem.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.groupitem.entity.GroupItem;

/**
 * 群成员MAPPER接口
 * @author lixinapp
 * @version 2024-09-20
 */
@Mapper
public interface GroupItemMapper extends BaseMapper<GroupItem> {

	/**
	 * 解除群成员禁言
	 * @param id 群成员ID
	 * @return 更新行数
	 */
	int unbanMember(@org.apache.ibatis.annotations.Param("id") String id);

}
