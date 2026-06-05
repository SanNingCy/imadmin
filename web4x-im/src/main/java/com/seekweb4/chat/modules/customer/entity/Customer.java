package com.seekweb4.chat.modules.customer.entity;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 平台参数Entity
 * @author lixinapp
 * @version 2024-10-11
 */
@Data
public class Customer extends DataEntity<Customer> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="登录方式 1：设备号登录 2：用户名密码登录 3：手机号密码登录 4：手机号、用户名密码登录", dictType="login_type", align=2, sort=1)
	private String loginType;		// 登录方式 1：设备号登录 2：用户名密码登录 3：手机号密码登录 4：手机号、用户名密码登录
	@ExcelField(title="是否有注册邀请码", dictType="yes_no", align=2, sort=2)
	private String hasInvit;		// 是否有注册邀请码
	@ExcelField(title="是否展示发现页菜单", dictType="yes_no", align=2, sort=3)
	private String showFx;		// 是否展示发现页菜单
	@ExcelField(title="是否展示小程序页菜单", dictType="yes_no", align=2, sort=4)
	private String showXcx;		// 是否展示小程序页菜单
	@ExcelField(title="是否可以编辑聊天消息", dictType="yes_no", align=2, sort=5)
	private String openBj;		// 是否可以编辑聊天消息
	@ExcelField(title="是否可以撤回聊天消息", dictType="yes_no", align=2, sort=6)
	private String openCh;		// 是否可以撤回聊天消息
	@ExcelField(title="是否开启密保问题", dictType="yes_no", align=2, sort=7)
	private String openMibao;		// 是否开启密保问题
	@ExcelField(title="收款人", align=2, sort=8)
	private String sname;		// 收款人
	@ExcelField(title="收款银行卡号", align=2, sort=9)
	private String sno;		// 收款银行卡号
	@ExcelField(title="收款码", align=2, sort=10)
	private String skm;		// 收款码
    @NotNull(message="单次发红包金额上限不能为空")
	@ExcelField(title="单次发红包金额上限", align=2, sort=11)
	private BigDecimal hongbao;		// 单次发红包金额上限
    @NotNull(message="单次转账金额上限不能为空")
	@ExcelField(title="单次转账金额上限", align=2, sort=12)
	private BigDecimal zhuanzhang;		// 单次转账金额上限
	@ExcelField(title="提现页大标题1", align=2, sort=13)
	private String tx1;		// 提现页大标题1
	@ExcelField(title="提现页大标题2", align=2, sort=14)
	private String tx2;		// 提现页大标题2
	@ExcelField(title="提现页大标题3", align=2, sort=15)
	private String tx3;		// 提现页大标题3
	@ExcelField(title="充值页图片标题", align=2, sort=16)
	private String cz;		// 充值页图片标题
	@ExcelField(title="充值页图片", align=2, sort=17)
	private String czimg;		// 充值页图片
	@ExcelField(title="是否开启红包功能", dictType="yes_no", align=2, sort=20)
	private String openhb;		// 是否开启红包功能
	@ExcelField(title="是否开启转账功能", dictType="yes_no", align=2, sort=21)
	private String openzz;		// 是否开启转账功能
	@ExcelField(title="是否开启语音通话功能", dictType="yes_no", align=2, sort=22)
	private String tonghua;		// 是否开启语音通话功能
	@ExcelField(title="是否开启位置发送功能", dictType="yes_no", align=2, sort=23)
	private String sendadd;		// 是否开启位置发送功能
	@ExcelField(title="是否展示客户悬浮按钮", dictType="yes_no", align=2, sort=24)
	private String showkf;		// 是否展示客户悬浮按钮
	@ExcelField(title="客服地址", align=2, sort=25)
	private String kfurl;		// 客服地址
	@ExcelField(title="昵称敏感词", align=2, sort=26)
	private String namemgc;		// 昵称敏感词
	@ExcelField(title="是否显示好友在线状态", dictType="yes_no", align=2, sort=27)
	private String showonline;		// 是否显示好友在线状态
	@ExcelField(title="是否展示手机消息已读状态", dictType="yes_no", align=2, sort=28)
	private String showyidu;		// 是否展示手机消息已读状态
	@ExcelField(title="是否展示签到红包", dictType="yes_no", align=2, sort=29)
	private String showsign;		// 是否展示签到红包
	@ExcelField(title="是否展示我的钱包", dictType="yes_no", align=2, sort=30)
	private String showqianbao;		// 是否展示我的钱包
    @NotNull(message="最低提现金额不能为空")
	@ExcelField(title="最低提现金额", align=2, sort=31)
	private BigDecimal mintixian;		// 最低提现金额
	@ExcelField(title="是否展示消息时间", dictType="yes_no", align=2, sort=32)
	private String showmsgtime;		// 是否展示消息时间
	@ExcelField(title="是否多端同步置顶状态", dictType="yes_no", align=2, sort=33)
	private String topsyan;		// 是否多端同步置顶状态

	private String openneibu;	//是否开启内部账号特定权限
	private String signtype;	//签到方式 1：随机发放 2：连签发放
	private String ipwhite;	//后台登录白名单
	private String changeeq;	//是否禁止更换登录设备
	private Integer eqcount;	//每个设备最多可注册个数设置（0为不限制）
	private Integer ipcount;	//单IP12小时内注册个数设置（0为不限制）

	private String filetype;	//聊天可发生文件类型（多种|隔开）
	private String regimgtype;	//登录注册页背景类型 1：图片 2：视频
	private String regimg;	//登录注册页背景图片
	private String regvideo;	//登录注册页背景视频
	private String reginfo;	//注册完成欢迎语

	private String aikey;	//ai机器人key
	private String robname;	//机器人昵称

	private Integer newadd;	//新号n小时内不可加人
	private Integer maxadd;	//最多可加多少人
	private Integer serchmin;	//搜索频率n分钟
	private Integer serchnum;	//搜索频率n次
	private String mgc;	//群发言敏感词

	private String line;	//是否上线

	public Customer() {
		super();
	}
	
	public Customer(String id){
		super(id);
	}
}