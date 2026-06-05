package com.seekweb4.chat.modules.black.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 拉黑表Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Black extends DataEntity<Black> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", align=2, sort=1)
	private String uid;		// 用户
	@ExcelField(title="拉黑对象", fieldType=Member.class, value="uid2.nickname", align=2, sort=2)
	private Member uid2;		// 拉黑对象
	
	public Black() {
		super();
	}
	
	public Black(String id){
		super(id);
	}
}