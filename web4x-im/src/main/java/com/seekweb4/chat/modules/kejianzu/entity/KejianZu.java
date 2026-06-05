package com.seekweb4.chat.modules.kejianzu.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 课件分组Entity
 * @author lixinapp
 * @version 2025-05-24
 */
@Data
public class KejianZu extends DataEntity<KejianZu> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="标题", align=2, sort=1)
	private String name;		// 标题
    // @NotNull(message="排序不能为空")
	@ExcelField(title="排序", align=2, sort=2)
	private Integer sort;		// 排序
	
	public KejianZu() {
		super();
	}
	
	public KejianZu(String id){
		super(id);
	}
}