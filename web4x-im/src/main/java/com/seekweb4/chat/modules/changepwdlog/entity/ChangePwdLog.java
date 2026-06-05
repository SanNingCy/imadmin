package com.seekweb4.chat.modules.changepwdlog.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 修改登录密码记录Entity
 * @author lixinapp
 * @version 2024-11-15
 */
@Data
public class ChangePwdLog extends DataEntity<ChangePwdLog> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.phone", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="旧密码", align=2, sort=2)
	private String oldPwd;		// 旧密码
	@ExcelField(title="新密码", align=2, sort=3)
	private String newPwd;		// 新密码
	@ExcelField(title="ip地址", align=2, sort=4)
	private String ip;		// ip地址
	@ExcelField(title="城市", align=2, sort=5)
	private String ipcity;		// 城市
	
	public ChangePwdLog() {
		super();
	}
	
	public ChangePwdLog(String id){
		super(id);
	}
}