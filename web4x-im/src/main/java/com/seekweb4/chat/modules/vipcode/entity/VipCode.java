package com.seekweb4.chat.modules.vipcode.entity;

import jakarta.validation.constraints.NotNull;
import com.seekweb4.chat.modules.member.entity.Member;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 会员码Entity
 * @author lixinapp
 * @version 2025-03-24
 */
@Data
public class VipCode extends DataEntity<VipCode> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="兑换码", align=2, sort=1)
	private String code;		// 兑换码
    @NotNull(message="会员天数不能为空")
	@ExcelField(title="会员天数", align=2, sort=2)
	private Integer day;		// 会员天数
	//@ExcelField(title="是否已被兑换", dictType="yes_no", align=2, sort=3)
	private String isdui;		// 是否已被兑换
	//@ExcelField(title="用户", fieldType=Member.class, value="u.idno", align=2, sort=4)
	private Member u;		// 用户
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private String idno;	// 兑换用户编号（与 day 同级别展示，来自 u.idno；未兑换时为空）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	//@ExcelField(title="兑换时间", align=2, sort=5)
	private Date duiTime;		// 兑换时间

	private Integer count;	//生成数量

	@JsonInclude(JsonInclude.Include.ALWAYS)
	@ExcelField(title="同步状态", dictType="yes_no", align=2, sort=6)
	private String syncStatus;	// 是否同步成功：0-未同步/失败，1-已同步成功

	@JsonInclude(JsonInclude.Include.ALWAYS)
	@ExcelField(title="类型", align=2, sort=7)
	private String type;		// 类型

	@JsonInclude(JsonInclude.Include.ALWAYS)
	@ExcelField(title="类型昵称", align=2, sort=8)
	private String typeName;	// 类型昵称
	
	public VipCode() {
		super();
	}
	
	public VipCode(String id){
		super(id);
	}
}