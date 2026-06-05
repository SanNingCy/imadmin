package com.seekweb4.chat.modules.express.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 物流公司Entity
 * @author lixinapp
 * @version 2023-02-06
 */
@Data
public class Express extends DataEntity<Express> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="名称", align=2, sort=1)
	private String name;		// 名称
	@ExcelField(title="编码", align=2, sort=2)
	private String code;		// 编码
    // @NotNull(message="排序不能为空")
	@ExcelField(title="排序", align=2, sort=3)
	private Integer sort;		// 排序
	
	public Express() {
		super();
	}
	
	public Express(String id){
		super(id);
	}
}