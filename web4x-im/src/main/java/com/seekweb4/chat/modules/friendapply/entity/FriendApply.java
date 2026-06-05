package com.seekweb4.chat.modules.friendapply.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 好友申请记录Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class FriendApply extends DataEntity<FriendApply> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="对方用户", fieldType=Member.class, value="uid2.nickname", align=2, sort=2)
	private Member uid2;		// 对方用户
	@ExcelField(title="申请信息", align=2, sort=3)
	private String sinfo;		// 申请信息
	@ExcelField(title="备注", align=2, sort=4)
	private String bei;		// 备注
	@ExcelField(title="描述", align=2, sort=5)
	private String info;		// 描述
	@ExcelField(title="状态 1：待审核 2：同意 3：拒绝", dictType="examine_state", align=2, sort=6)
	private String state;		// 状态 1：待审核 2：同意 3：拒绝

	private String key;
	
	public FriendApply() {
		super();
	}
	
	public FriendApply(String id){
		super(id);
	}
}