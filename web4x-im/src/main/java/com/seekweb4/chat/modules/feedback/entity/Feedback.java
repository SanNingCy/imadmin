package com.seekweb4.chat.modules.feedback.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 意见反馈Entity
 * @author lixinapp
 * @version 2023-08-30
 */
@Data
public class Feedback extends DataEntity<Feedback> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", fieldType=Member.class, value="member.phone", align=2, sort=1)
	private Member member;		// 用户
	@ExcelField(title="联系方式", align=2, sort=2)
	private String phone;		// 联系方式
	@ExcelField(title="内容", align=2, sort=3)
	private String content;		// 内容
	@ExcelField(title="图片", align=2, sort=4)
	private String images;		// 图片

	/** 管理员回复 */
	@ExcelField(title="管理员回复", align=2, sort=5)
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private String reply;
	/** 回复时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonInclude(JsonInclude.Include.ALWAYS)
	@ExcelField(title="回复时间", align=2, sort=6)
	private Date replyDate;
	/** 0=待处理 1=已回复 2=已采纳 */
	@ExcelField(title="状态", align=2, sort=7)
	private Integer status;
	/** 采纳时奖励代币数量（由后台配置） */
	@ExcelField(title="奖励数量", align=2, sort=8)
	private BigDecimal rewardAmount;
	/** 是否已发放奖励 0=否 1=是 */
	@ExcelField(title="是否已发奖", align=2, sort=9)
	private Integer isReward;

	/** 列表查询：按用户 id 号（模糊）；不落库、不出现在保存 JSON */
	@JsonIgnore
	private String idno;
	
	public Feedback() {
		super();
	}
	
	public Feedback(String id){
		super(id);
	}
}