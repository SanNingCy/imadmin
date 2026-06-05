package com.seekweb4.chat.modules.tixian.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import java.math.BigDecimal;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 提现申请Entity
 * @author lixinapp
 * @version 2024-09-22
 */
@Data
public class Tixian extends DataEntity<Tixian> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="金额", align=2, sort=2)
	private BigDecimal money;		// 金额
	@ExcelField(title="图片", align=2, sort=3)
	private String img;		// 图片
	@ExcelField(title="提现信息", align=2, sort=4)
	private String info;		// 提现信息
	@ExcelField(title="状态 1：待处理 2：通过 3：驳回 4：已锁定", dictType="examine_state", align=2, sort=5)
	private String state;		// 状态 1：待处理 2：通过 3：驳回 4：已锁定

	private String imgtitle;	//图片标题
	private String reason;	//驳回原因
	
	public Tixian() {
		super();
	}
	
	public Tixian(String id){
		super(id);
	}
}