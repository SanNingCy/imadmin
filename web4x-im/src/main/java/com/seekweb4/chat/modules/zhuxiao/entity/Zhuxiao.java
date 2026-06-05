package com.seekweb4.chat.modules.zhuxiao.entity;


import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 注销申请Entity
 * @author lixinapp
 * @version 2025-07-01
 */
@Data
public class Zhuxiao extends DataEntity<Zhuxiao> {
	
	private static final long serialVersionUID = 1L;
	private String uid;
	@ExcelField(title="设备号", align=2, sort=1)
	private String eqid;		// 设备号
	@ExcelField(title="账号名", align=2, sort=2)
	private String acc;		// 账号名
	@ExcelField(title="id号", align=2, sort=3)
	private String idno;		// id号
	@ExcelField(title="审核状态 ", align=2, sort=4)
	private String state;		// 审核状态 
	@ExcelField(title="驳回原因", align=2, sort=5)
	private String reason;		// 驳回原因
	
	public Zhuxiao() {
		super();
	}
	
	public Zhuxiao(String id){
		super(id);
	}
}