package com.seekweb4.chat.modules.rechagelog.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 充值记录Entity
 * @author lixinapp
 * @version 2024-09-22
 */
@Data
public class RechageLog extends DataEntity<RechageLog> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="金额", align=2, sort=2)
	private String money;		// 金额
	@ExcelField(title="标题", align=2, sort=3)
	private String title;		// 标题
	@ExcelField(title="收款人", align=2, sort=4)
	private String sname;		// 收款人
	@ExcelField(title="收款卡号", align=2, sort=5)
	private String sno;		// 收款卡号
	@ExcelField(title="打款凭证", align=2, sort=6)
	private String pingz;		// 打款凭证

	private String state;	//审核状态 1：待审核 2：通过 3：驳回 4：已锁定
	
	public RechageLog() {
		super();
	}
	
	public RechageLog(String id){
		super(id);
	}
}