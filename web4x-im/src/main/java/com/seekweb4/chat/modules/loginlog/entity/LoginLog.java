package com.seekweb4.chat.modules.loginlog.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 登录记录Entity
 * @author lixinapp
 * @version 2024-11-15
 */
@Data
public class LoginLog extends DataEntity<LoginLog> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.phone", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="ip地址", align=2, sort=2)
	private String ip;		// ip地址
	@ExcelField(title="城市", align=2, sort=3)
	private String ipcity;		// 城市
	@ExcelField(title="设备号", align=2, sort=4)
	private String eqno;		// 设备号
	@ExcelField(title="二维码", align=2, sort=5)
	private String qrcode;		// 二维码
	@ExcelField(title="登录结果", align=2, sort=6)
	private Integer status;		// 登录结果 1：成功 0：失败
	@ExcelField(title="失败原因", align=2, sort=7)
	private String failRemark;	// 失败原因（对应列 fail_remark）
	
	public LoginLog() {
		super();
	}
	
	public LoginLog(String id){
		super(id);
	}
}