package com.seekweb4.chat.modules.grouphongbaolog.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.group.entity.Group;
import java.math.BigDecimal;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 群红包领取记录Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class GroupHongbaoLog extends DataEntity<GroupHongbaoLog> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="红包", align=2, sort=2)
	private String baoId;		// 红包
	@ExcelField(title="群组", fieldType=Group.class, value="group.name", align=2, sort=3)
	private Group group;		// 群组
	@ExcelField(title="领取金额", align=2, sort=4)
	private BigDecimal money;		// 领取金额
	
	public GroupHongbaoLog() {
		super();
	}
	
	public GroupHongbaoLog(String id){
		super(id);
	}
}