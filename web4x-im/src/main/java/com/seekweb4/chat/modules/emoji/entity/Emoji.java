package com.seekweb4.chat.modules.emoji.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 表情包Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Emoji extends DataEntity<Emoji> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="表情", align=2, sort=2)
	private String img;		// 表情
	
	public Emoji() {
		super();
	}
	
	public Emoji(String id){
		super(id);
	}
}