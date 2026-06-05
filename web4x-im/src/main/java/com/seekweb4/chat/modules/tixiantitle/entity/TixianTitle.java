package com.seekweb4.chat.modules.tixiantitle.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 提现页标题Entity
 * @author lixinapp
 * @version 2024-09-22
 */
@Data
public class TixianTitle extends DataEntity<TixianTitle> {
	
	private static final long serialVersionUID = 1L;
    // @NotNull(message="排序不能为空")
	@ExcelField(title="排序", align=2, sort=1)
	private Integer sort;		// 排序
	@ExcelField(title="标题", align=2, sort=2)
	private String title;		// 标题
	@ExcelField(title="默认文字", align=2, sort=3)
	private String info;		// 默认文字
	
	public TixianTitle() {
		super();
	}
	
	public TixianTitle(String id){
		super(id);
	}
}