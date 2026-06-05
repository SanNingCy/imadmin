package com.seekweb4.chat.modules.signlog.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import java.math.BigDecimal;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 签到记录Entity
 * @author lixinapp
 * @version 2024-09-22
 */
@Data
public class SignLog extends DataEntity<SignLog> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="是否签到 1：是 0：否", align=2, sort=2)
	private String isSign;		// 是否签到 1：是 0：否
	@ExcelField(title="金额", align=2, sort=3)
	private BigDecimal money;		// 金额
	@ExcelField(title="日期（yyyy-mm-dd）", align=2, sort=4)
	private String date;		// 日期（yyyy-mm-dd）
	@ExcelField(title="第几天", align=2, sort=5)
	private Integer day;		// 第几天
	
	public SignLog() {
		super();
	}
	
	public SignLog(String id){
		super(id);
	}
}