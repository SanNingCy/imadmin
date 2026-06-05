package com.seekweb4.chat.modules.xcx.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 小程序链接管理Entity
 * @author lixinapp
 * @version 2024-09-22
 */
@Data
public class Xcx extends DataEntity<Xcx> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="小程序名", align=2, sort=1)
	private String title;		// 小程序名
	@ExcelField(title="小程序码", align=2, sort=2)
	private String code;		// 小程序码
	@ExcelField(title="地址", align=2, sort=3)
	private String url;		// 地址
    // @NotNull(message="排序不能为空")
	@ExcelField(title="排序", align=2, sort=4)
	private Integer sort;		// 排序
	
	public Xcx() {
		super();
	}
	
	public Xcx(String id){
		super(id);
	}
}