package com.seekweb4.chat.modules.agreement.entity;


import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 说明协议Entity
 * @author lixinapp
 * @version 2021-07-05
 */
@Data
public class Agreement extends DataEntity<Agreement> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="标题", align=2, sort=1)
	private String title;		// 标题
	@ExcelField(title="内容", align=2, sort=2)
	private String content;		// 内容
	
	public Agreement() {
		super();
	}
	
	public Agreement(String id){
		super(id);
	}
}