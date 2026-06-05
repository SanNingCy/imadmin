package com.seekweb4.chat.modules.memberChangeLog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.sys.entity.User;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息修改记录统一VO
 * 用于统一展示修改昵称、密码、手机号、支付密码等记录
 * @author system
 * @version 2025-12-05
 */
@Data
public class MemberChangeLogVO {
	
	/**
	 * 记录ID
	 */
	private String id;
	
	/**
	 * 用户信息
	 */
	private Member u;
	
	/**
	 * 修改类型
	 * nickname: 修改昵称
	 * password: 修改登录密码
	 * phone: 修改手机号
	 * paypwd: 修改支付密码
	 */
	private String changeType;
	
	/**
	 * 修改类型名称
	 */
	private String changeTypeName;
	
	/**
	 * 旧值（根据类型不同，可能是旧昵称、旧密码、旧手机号等）
	 */
	private String oldValue;
	
	/**
	 * 新值（根据类型不同，可能是新昵称、新密码、新手机号等）
	 */
	private String newValue;
	
	/**
	 * IP地址
	 */
	private String ip;
	
	/**
	 * 城市
	 */
	private String ipcity;
	
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createDate;
	
	/**
	 * 创建人
	 */
	private User createBy;
	
}

