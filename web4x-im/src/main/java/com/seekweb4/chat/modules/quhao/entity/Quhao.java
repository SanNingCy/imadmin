package com.seekweb4.chat.modules.quhao.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 手机区号Entity
 * @author lixinapp
 * @version 2024-09-24
 */
@Data
public class Quhao extends DataEntity<Quhao> {
	
	private static final long serialVersionUID = 1L;
    // @NotNull(message="排序不能为空")
	@ExcelField(title="排序", align=2, sort=1)
	private Integer sort;		// 排序
	@ExcelField(title="区号", align=2, sort=2)
	private String no;		// 区号
	@ExcelField(title="国家", align=2, sort=3)
	private String country;		// 国家
	@ExcelField(title="扩展字段", align=2, sort=4)
	private String ext;		// 扩展字段
	
	public Quhao() {
		super();
	}
	
	public Quhao(String id){
		super(id);
	}
}