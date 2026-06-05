package com.seekweb4.chat.modules.dyomm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seekweb4.chat.modules.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import com.seekweb4.chat.modules.dy.entity.Dy;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 动态评论Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class DyComm extends DataEntity<DyComm> {
	
	private static final long serialVersionUID = 1L;
    @NotNull(message="用户不能为空")
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
    @NotNull(message="动态不能为空")
	@ExcelField(title="动态", fieldType=Dy.class, value="dy.info", align=2, sort=2)
	private Dy dy;		// 动态
	@ExcelField(title="回复给谁", fieldType=Member.class, value="to.nickname", align=2, sort=3)
	private Member to;		// 回复给谁
	@ExcelField(title="内容", align=2, sort=4)
	private String title;		// 内容
	@ExcelField(title="评论id", align=2, sort=5)
	private String commid;		// 评论id
	@ExcelField(title="点赞数", align=2, sort=6)
	private Integer likes;		// 点赞数
	@ExcelField(title="层级", align=2, sort=7)
	private Integer level;		// 层级

	/**
	 * 仅列表查询用：前端传 {@code idno}（非 {@code u.idno}）时按评论用户 idno / 靓号筛选；不落库、不出参 JSON。
	 */
	@JsonIgnore
	private String idno;

	public DyComm() {
		super();
	}
	
	public DyComm(String id){
		super(id);
	}
}