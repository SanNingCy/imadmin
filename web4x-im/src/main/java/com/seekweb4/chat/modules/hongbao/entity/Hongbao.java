package com.seekweb4.chat.modules.hongbao.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 单聊红包记录Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Hongbao extends DataEntity<Hongbao> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="发红包人", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 发红包人
	@ExcelField(title="发送对象", fieldType=Member.class, value="uid2.nickname", align=2, sort=2)
	private Member uid2;		// 发送对象
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
	
	public Hongbao() {
		super();
	}
	
	public Hongbao(String id){
		super(id);
	}
}