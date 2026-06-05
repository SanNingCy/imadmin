package com.seekweb4.chat.modules.dy.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 朋友圈动态Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Dy extends DataEntity<Dy> {
	
	private static final long serialVersionUID = 1L;
    @NotNull(message="用户不能为空")
	@ExcelField(title="用户", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 用户
	@ExcelField(title="内容", align=2, sort=2)
	private String info;		// 内容
	@ExcelField(title="图片", align=2, sort=3)
	private String imgs;		// 图片
	@ExcelField(title="视频封面", align=2, sort=4)
	private String vimg;		// 视频封面
	@ExcelField(title="视频", align=2, sort=5)
	private String video;		// 视频
	@ExcelField(title="1：图片动态 2：视频动态", align=2, sort=6)
	private String type;		// 1：图片动态 2：视频动态
	@ExcelField(title="城市", align=2, sort=7)
	private String address;		// 城市
	@ExcelField(title="经度", align=2, sort=8)
	private String lon;		// 经度
	@ExcelField(title="lat", align=2, sort=9)
	private String lat;		// lat
	
	public Dy() {
		super();
	}
	
	public Dy(String id){
		super(id);
	}
}