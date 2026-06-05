package com.seekweb4.chat.modules.friend.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 好友关系Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Friend extends DataEntity<Friend> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="好友", fieldType=Member.class, value="uid2.nickname", align=2, sort=2)
	private Member uid2;		// 好友
	@ExcelField(title="首字母", align=2, sort=3)
	private String zimu;		// 首字母
	@ExcelField(title="备注", align=2, sort=4)
	private String bei;		// 备注
	@ExcelField(title="是否免打扰", dictType="yes_no", align=2, sort=5)
	private String mdr;		// 是否免打扰
	@ExcelField(title="是否置顶", dictType="yes_no", align=2, sort=6)
	private String isTop;		// 是否置顶

	private String isfanyi;	//是否自动翻译

	private String key;
	
	public Friend() {
		super();
	}
	
	public Friend(String id){
		super(id);
	}
}