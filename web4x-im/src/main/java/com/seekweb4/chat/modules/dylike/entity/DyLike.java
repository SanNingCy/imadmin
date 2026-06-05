package com.seekweb4.chat.modules.dylike.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 动态点赞Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class DyLike extends DataEntity<DyLike> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="动态", align=2, sort=2)
	private String dyId;		// 动态
	
	public DyLike() {
		super();
	}
	
	public DyLike(String id){
		super(id);
	}
}