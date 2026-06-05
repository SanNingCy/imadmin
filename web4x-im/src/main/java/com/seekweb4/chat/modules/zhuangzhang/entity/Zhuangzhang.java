package com.seekweb4.chat.modules.zhuangzhang.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 转账记录Entity
 * @author lixinapp
 * @version 2024-09-22
 */
@Data
public class Zhuangzhang extends DataEntity<Zhuangzhang> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="转账人", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 转账人
	@ExcelField(title="转账对象", fieldType=Member.class, value="uid2.nickname", align=2, sort=2)
	private Member uid2;		// 转账对象
	@ExcelField(title="金额", align=2, sort=3)
	private BigDecimal money;		// 金额
	@ExcelField(title="描述", align=2, sort=4)
	private String info;		// 描述
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="退款时间", align=2, sort=5)
	private Date tuiTime;		// 退款时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="领取时间", align=2, sort=6)
	private Date shouTime;		// 领取时间
	
	public Zhuangzhang() {
		super();
	}
	
	public Zhuangzhang(String id){
		super(id);
	}
}