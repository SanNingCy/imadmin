package com.seekweb4.chat.modules.group.entity;

import com.seekweb4.chat.modules.member.entity.Member;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 群组信息Entity
 * @author lixinapp
 * @version 2024-09-20
 */
@Data
public class Group extends DataEntity<Group> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="群主", fieldType=Member.class, value="u.nickname", align=2, sort=1)
	private Member u;		// 群主
	@ExcelField(title="群头像", align=2, sort=2)
	private String icon;		// 群头像
	@ExcelField(title="群组名称", align=2, sort=3)
	private String name;		// 群组名称
	@ExcelField(title="id号", align=2, sort=4)
	private String idno;		// id号
	@ExcelField(title="群名片二维码", align=2, sort=5)
	private String qrcode;		// 群名片二维码
	@ExcelField(title="群公告", align=2, sort=6)
	private String gonggao;		// 群公告
	@ExcelField(title="群员邀请好友进群-是否开启", dictType="yes_no", align=2, sort=7)
	private String openQyyq;		// 群员邀请好友进群-是否开启
	@ExcelField(title="群员入群邀请验证-是否开启", dictType="yes_no", align=2, sort=8)
	private String openQyyz;		// 群员入群邀请验证-是否开启
	@ExcelField(title="仅群主及管理员可发红包-是否开启", dictType="yes_no", align=2, sort=9)
	private String openQzhb;		// 仅群主及管理员可发红包-是否开启
	@ExcelField(title="群主及管理员可推送名片-是否开启", dictType="yes_no", align=2, sort=10)
	private String openQzts;		// 群主及管理员可推送名片-是否开启
	@ExcelField(title="群员入群邀请管理验证-是否开启", dictType="yes_no", align=2, sort=11)
	private String openQygl;		// 群员入群邀请管理验证-是否开启
	@ExcelField(title="群员无法私聊加好友-是否开启", dictType="yes_no", align=2, sort=12)
	private String openQysl;		// 群员无法私聊加好友-是否开启
	@ExcelField(title="群员仅可见群主及管理员-是否开启", dictType="yes_no", align=2, sort=13)
	private String openQykj;		// 群员仅可见群主及管理员-是否开启
	@ExcelField(title="群员发言间隔秒数", align=2, sort=14)
	private Integer jiange;		// 群员发言间隔秒数
	@ExcelField(title="敏感词", align=2, sort=15)
	private String mgc;		// 敏感词
	@ExcelField(title="触发敏感词是否禁言-是否开启", dictType="yes_no", align=2, sort=16)
	private String openGmc;		// 触发敏感词是否禁言-是否开启
	@ExcelField(title="全员禁言-是否开启", dictType="yes_no", align=2, sort=17)
	private String allJy;		// 全员禁言-是否开启

	private String gtype;	//群组大小 1：一千 2：五千

	private String aikey;	//ai机器人key

	private String openpic;	//是否开启图片AI回复
	
	public Group() {
		super();
	}
	
	public Group(String id){
		super(id);
	}
}