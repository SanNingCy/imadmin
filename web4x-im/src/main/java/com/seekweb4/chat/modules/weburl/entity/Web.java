package com.seekweb4.chat.modules.weburl.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 发现页外链Entity
 * @author lixinapp
 * @version 2024-09-22
 */
@Data
public class Web extends DataEntity<Web> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="标题", align=2, sort=1)
	private String title;		// 标题
	@ExcelField(title="图标", align=2, sort=2)
	private String icon;		// 图标
	@ExcelField(title="地址", align=2, sort=3)
	private String url;		// 地址
    // @NotNull(message="排序不能为空")
	@ExcelField(title="排序", align=2, sort=4)
	private Integer sort;		// 排序
	
	public Web() {
		super();
	}
	
	public Web(String id){
		super(id);
	}
}