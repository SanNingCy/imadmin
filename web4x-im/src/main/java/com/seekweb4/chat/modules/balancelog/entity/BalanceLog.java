package com.seekweb4.chat.modules.balancelog.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import java.math.BigDecimal;
import java.util.Date;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 余额明细Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class BalanceLog extends DataEntity<BalanceLog> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="标题", align=2, sort=2)
	private String title;		// 标题
	@ExcelField(title="金额", align=2, sort=3)
	private BigDecimal money;		// 金额
	@ExcelField(title="1：收入 0：支出", dictType="shouzhi", align=2, sort=4)
	private String state;		// 1：收入 0：支出
	@ExcelField(title="类型 1：红包 2：转账 3：充值 4：签到", dictType="balance_type", align=2, sort=5)
	private String type;		// 类型 1：红包 2：转账 3：充值 4：签到

	private String info;	//备注

	/** 查询条件：用户ID号（idno） */
	private String idno;

	/** 查询条件：用户靓号（lianghao） */
	private String lianghao;
	
	private Date beginCreateDate;		// 开始 创建时间
	private Date endCreateDate;		// 结束 创建时间
	
	// 以下字段用于返回数据，不持久化到数据库
	private BigDecimal beforeBalance;	// 操作前的余额
	private BigDecimal afterBalance;		// 操作后的余额
	private BigDecimal currentBalance;	// 用户当前余额
	
	public BalanceLog() {
		super();
	}
	
	public BalanceLog(String id){
		super(id);
	}
}