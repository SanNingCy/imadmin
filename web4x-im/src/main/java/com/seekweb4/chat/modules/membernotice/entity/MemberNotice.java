package com.seekweb4.chat.modules.membernotice.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 用户系统消息Entity
 * @author lixinapp
 * @version 2024-12-23
 */
@Data
public class MemberNotice extends DataEntity<MemberNotice> {
	
	private static final long serialVersionUID = 1L;
    @NotNull(message="用户不能为空")
	@ExcelField(title="用户", fieldType=Member.class, value="u.idno", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="内容", align=2, sort=2)
	private String info;		// 内容
	@ExcelField(title="是否已读", dictType="yes_no", align=2, sort=3)
	private String isdu;		// 是否已读
	@ExcelField(title="系统公告id", align=2, sort=4)
	private String nid;		// 系统公告id
	
	public MemberNotice() {
		super();
	}
	
	public MemberNotice(String id){
		super(id);
	}
}