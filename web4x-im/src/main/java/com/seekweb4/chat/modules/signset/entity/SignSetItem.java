package com.seekweb4.chat.modules.signset.entity;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import com.seekweb4.chat.core.persistence.DataEntity;
import lombok.Data;

/**
 * 连签额外奖励Entity
 * @author lixinapp
 * @version 2024-11-26
 */
@Data
public class SignSetItem extends DataEntity<SignSetItem> {
	
	private static final long serialVersionUID = 1L;
	private SignSet sid;		// 签到规则 父类
	@NotNull(message="连签天数不能为空")
	@ExcelField(title="连签天数", align=2, sort=2)
	private Integer day;		// 连签天数
	@NotNull(message="追加金额不能为空")
	@ExcelField(title="追加金额", align=2, sort=3)
	private BigDecimal money;		// 追加金额
	
	public SignSetItem() {
		super();
	}

	public SignSetItem(String id){
		super(id);
	}

	public SignSetItem(SignSet sid){
		this.sid = sid;
	}

}