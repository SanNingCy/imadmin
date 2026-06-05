package com.seekweb4.chat.modules.hudong.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 互动消息Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Hudong extends DataEntity<Hudong> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="操作人", fieldType=Member.class, value="uid2.nickname", align=2, sort=2)
	private Member uid2;		// 操作人
	@ExcelField(title="内容", align=2, sort=3)
	private String info;		// 内容
	@ExcelField(title="类型 1：点赞 2：评论", align=2, sort=4)
	private String type;		// 类型 1：点赞 2：评论
	@ExcelField(title="1：已读 0：未读", align=2, sort=5)
	private String state;		// 1：已读 0：未读
	@ExcelField(title="动态", align=2, sort=6)
	private String dyId;		// 动态
	private String dyInfo;
	private String dyImgs;

	public Hudong() {
		super();
	}
	
	public Hudong(String id){
		super(id);
	}
}