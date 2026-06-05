package com.seekweb4.chat.modules.groupapply.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.group.entity.Group;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 入群申请Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class GroupApply extends DataEntity<GroupApply> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="邀请人", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 邀请人
	@ExcelField(title="被邀请人", fieldType=Member.class, value="uid2.nickname", align=2, sort=2)
	private Member uid2;		// 被邀请人
	@ExcelField(title="群组", fieldType=Group.class, value="group.name", align=2, sort=3)
	private Group group;		// 群组
	@ExcelField(title="描述", align=2, sort=4)
	private String info;		// 描述
	@ExcelField(title="状态：1：待处理 2：已同意 3：已拒绝", dictType="examine_state", align=2, sort=5)
	private String state;		// 状态：1：待处理 2：已同意 3：已拒绝
	@ExcelField(title="类型 1：用户收到的 2：群管理员、群主收到的", align=2, sort=6)
	private String type;		// 类型 1：用户收到的 2：群管理员、群主收到的
	@ExcelField(title="可查看人ids", align=2, sort=7)
	private String showids;		// 可查看人ids

	private String key;
	
	public GroupApply() {
		super();
	}
	
	public GroupApply(String id){
		super(id);
	}
}