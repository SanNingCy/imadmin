package com.seekweb4.chat.modules.coll.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 收藏记录Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Coll extends DataEntity<Coll> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="用户", align=2, sort=1)
	private String uid;		// 用户
    @NotNull(message="对方用户不能为空")
	@ExcelField(title="对方用户", fieldType=Member.class, value="uid2.nickname", align=2, sort=2)
	private Member uid2;		// 对方用户
	@ExcelField(title="内容", align=2, sort=3)
	private String info;		// 内容
	@ExcelField(title="类型 1：文字2：图片3：视频4：音频5：位置", dictType="coll_type", align=2, sort=4)
	private String type;		// 类型 1：文字2：图片3：视频4：音频5：位置
	@ExcelField(title="音频秒数", align=2, sort=5)
	private Integer miao;		// 音频秒数
	@ExcelField(title="经度", align=2, sort=6)
	private String lon;		// 经度
	@ExcelField(title="纬度", align=2, sort=7)
	private String lat;		// 纬度
	@ExcelField(title="消息id", align=2, sort=8)
	private String msgid;		// 消息id
	
	public Coll() {
		super();
	}
	
	public Coll(String id){
		super(id);
	}
}