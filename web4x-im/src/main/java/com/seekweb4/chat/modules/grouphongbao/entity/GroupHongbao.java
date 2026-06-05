package com.seekweb4.chat.modules.grouphongbao.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.group.entity.Group;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 群红包Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class GroupHongbao extends DataEntity<GroupHongbao> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="发红包用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 发红包用户
	@ExcelField(title="群组", fieldType=Group.class, value="group.name", align=2, sort=2)
	private Group group;		// 群组
	@ExcelField(title="类型 1：拼手气 2：普通 3：专属", dictType="hongbao_type", align=2, sort=3)
	private String type;		// 类型 1：拼手气 2：普通 3：专属
	@ExcelField(title="总金额", align=2, sort=4)
	private BigDecimal money;		// 总金额
	@ExcelField(title="symonet", align=2, sort=5)
	private BigDecimal symonet;		// symonet
	@ExcelField(title="个数", align=2, sort=6)
	private Integer count;		// 个数
	@ExcelField(title="剩余个数", align=2, sort=7)
	private Integer sycount;		// 剩余个数
	@ExcelField(title="描述", align=2, sort=8)
	private String info;		// 描述
	@ExcelField(title="专属人", align=2, sort=9)
	private String zsid;		// 专属人
	private String zsname;
	@ExcelField(title="退款金额", align=2, sort=10)
	private BigDecimal tuiMoney;		// 退款金额
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="退款时间", align=2, sort=11)
	private Date tuiTime;		// 退款时间
	private Date beginCreateDate;		// 开始 添加时间
	private Date endCreateDate;		// 结束 添加时间
	
	public GroupHongbao() {
		super();
	}
	
	public GroupHongbao(String id){
		super(id);
	}
}