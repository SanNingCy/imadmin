package com.seekweb4.chat.modules.tousu.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.group.entity.Group;
import java.util.Date;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 投诉群组记录Entity
 * @author lixinapp
 * @version 2024-09-22
 */
@Data
public class Tousu extends DataEntity<Tousu> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="群组", fieldType=Group.class, value="group.name", align=2, sort=2)
	private Group group;		// 群组
	@ExcelField(title="投诉原因", align=2, sort=3)
	private String reason;		// 投诉原因
	@ExcelField(title="聊天记录", align=2, sort=4)
	private String log;		// 聊天记录
	@ExcelField(title="图片", align=2, sort=5)
	private String imgs;		// 图片
	@ExcelField(title="描述", align=2, sort=6)
	private String info;		// 描述
	private Date beginCreateDate;		// 开始 添加时间
	private Date endCreateDate;		// 结束 添加时间
	
	public Tousu() {
		super();
	}
	
	public Tousu(String id){
		super(id);
	}
}