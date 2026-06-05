package com.seekweb4.chat.modules.mibaofaq.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 密保问题Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class MibaoFaq extends DataEntity<MibaoFaq> {
	
	private static final long serialVersionUID = 1L;
    // @NotNull(message="排序不能为空")
	@ExcelField(title="排序", align=2, sort=1)
	private Integer sort;		// 排序
	@ExcelField(title="问题", align=2, sort=2)
	private String title;		// 问题
	
	public MibaoFaq() {
		super();
	}
	
	public MibaoFaq(String id){
		super(id);
	}
}