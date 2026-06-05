package com.seekweb4.chat.modules.groupuplog.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.group.entity.Group;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 群升级记录Entity
 * @author lixinapp
 * @version 2025-03-24
 */
@Data
public class GroupUplog extends DataEntity<GroupUplog> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="操作用户", fieldType=Member.class, value="u.idno", align=2, sort=1)
	private Member u;		// 操作用户
	@ExcelField(title="群组", fieldType=Group.class, value="group.name", align=2, sort=2)
	private Group group;		// 群组
	@ExcelField(title="群主", fieldType=Member.class, value="qz.idno", align=2, sort=3)
	private Member qz;		// 群主
	
	public GroupUplog() {
		super();
	}
	
	public GroupUplog(String id){
		super(id);
	}
}