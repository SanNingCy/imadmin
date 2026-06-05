package com.seekweb4.chat.modules.faq.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 常见问题Entity
 * @author lixinapp
 * @version 2022-12-19
 */
@Data
public class Faq extends DataEntity<Faq> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="标题", align=2, sort=1)
	private String title;		// 标题
	@ExcelField(title="内容", align=2, sort=2)
	private String content;		// 内容
	@ExcelField(title="语言", align=2, sort=3)
	private String lang;		// 语言类型(zh/en)
    // @NotNull(message="排序不能为空")
	@ExcelField(title="排序", align=2, sort=4)
	private Integer sort;		// 排序
	
	public Faq() {
		super();
	}
	
	public Faq(String id){
		super(id);
	}
}