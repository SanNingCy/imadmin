package com.seekweb4.chat.modules.sign.entity;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 签到奖励配置Entity
 * @author lixinapp
 * @version 2024-09-22
 */
@Data
public class Sign extends DataEntity<Sign> {
	
	private static final long serialVersionUID = 1L;
	@NotNull(message="连续签到次数")
	private Integer day;
	@NotNull(message="用户金额不能为空")
	private BigDecimal money;
	@NotNull(message="最后一次签到时间不能为空")
	private String lastSignDate;
	@NotNull(message="奖励金额不能为空")
	private BigDecimal awardMoney;
	
	public Sign() {
		super();
	}
	
	public Sign(String id){
		super(id);
	}
}