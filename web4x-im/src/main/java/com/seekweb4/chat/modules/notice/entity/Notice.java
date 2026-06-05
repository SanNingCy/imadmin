package com.seekweb4.chat.modules.notice.entity;


import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 系统通知Entity
 * @author lixinapp
 * @version 2024-12-23
 */
@Data
public class Notice extends DataEntity<Notice> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="内容", align=2, sort=1)
	private String info;		// 内容
	
	public Notice() {
		super();
	}
	
	public Notice(String id){
		super(id);
	}
}