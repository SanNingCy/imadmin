package com.seekweb4.chat.modules.groupitem.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.group.entity.Group;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 群成员Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class GroupItem extends DataEntity<GroupItem> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="群组", fieldType=Group.class, value="group.name", align=2, sort=2)
	private Group group;		// 群组
	@ExcelField(title="群昵称", align=2, sort=3)
	private String nickname;		// 群昵称
	@ExcelField(title="身份 1：群主 2：管理 3：成员", dictType="group_user_type", align=2, sort=4)
	private String type;		// 身份 1：群主 2：管理 3：成员
	@ExcelField(title="是否被禁言", dictType="yes_no", align=2, sort=5)
	private String isjy;		// 是否被禁言
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="禁言到期时间", align=2, sort=6)
	private Date jyTime;		// 禁言到期时间
	private String zimu;	//首字母
	private String isfanyi;	//是否自动翻译

	private String key;
	public GroupItem() {
		super();
	}
	
	public GroupItem(String id){
		super(id);
	}
}