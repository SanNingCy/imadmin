package com.seekweb4.chat.modules.signset.entity;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import com.google.common.collect.Lists;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 签到奖励配置Entity
 * @author lixinapp
 * @version 2024-11-26
 */
@Data
public class SignSet extends DataEntity<SignSet> {
	
	private static final long serialVersionUID = 1L;
	@NotNull(message="最低奖励金额不能为空")
	@ExcelField(title="最低奖励金额", align=2, sort=1)
	private BigDecimal min;		// 最低奖励金额
	@NotNull(message="最高奖励金额不能为空")
	@ExcelField(title="最高奖励金额", align=2, sort=2)
	private BigDecimal max;		// 最高奖励金额
	@NotNull(message="连签每日金额不能为空")
	@ExcelField(title="连签每日金额", align=2, sort=3)
	private BigDecimal daymoney;		// 连签每日金额
	private List<SignSetItem> signSetItemList = Lists.newArrayList();		// 子表列表
	
	public SignSet() {
		super();
	}

	public SignSet(String id){
		super(id);
	}
}