package com.seekweb4.chat.modules.upgrade.entity;

import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 版本更新Entity
 * @author lixinapp
 * @version 2022-12-19
 */
@Data
public class Upgrade extends DataEntity<Upgrade> {
	
	private static final long serialVersionUID = 1L;
    @NotNull(message="升级编号不能为空")
	@ExcelField(title="升级编号", align=2, sort=1)
	private Integer number;		// 升级编号
	@ExcelField(title="版本号", align=2, sort=2)
	private String version;		// 版本号
	@ExcelField(title="地址", align=2, sort=3)
	private String url;		// 地址
	@ExcelField(title="更新内容", align=2, sort=4)
	private String content;		// 更新内容
	@ExcelField(title="强制更新 0否 1是", dictType="yes_no", align=2, sort=5)
	private String type;		// 强制更新 0否 1是
	@ExcelField(title="二维码", align=2, sort=6)
	private String qrCode;     // 二维码地址
	
	public Upgrade() {
		super();
		this.setIdType(IDTYPE_AUTO);
	}
	
	public Upgrade(String id){
		super(id);
	}
}