package com.seekweb4.chat.modules.reason.entity;


import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 投诉原因Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Reason extends DataEntity<Reason> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="原因", align=2, sort=1)
	private String title;		// 原因
	
	public Reason() {
		super();
	}
	
	public Reason(String id){
		super(id);
	}
}