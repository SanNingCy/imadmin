package com.seekweb4.chat.modules.redPacketTransaction.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seekweb4.chat.modules.group.entity.Group;
import com.seekweb4.chat.modules.member.entity.Member;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包交易记录统一VO
 * 用于统一展示单聊红包和群聊红包记录
 * @author system
 * @version 2025-12-08
 */
@Data
public class RedPacketTransactionVO {
	
	/**
	 * 记录ID
	 */
	private String id;
	
	/**
	 * 发红包用户
	 */
	private Member u;
	
	/**
	 * 接收红包用户（单聊红包）
	 */
	private Member uid2;
	
	/**
	 * 群组（群聊红包）
	 */
	private Group group;
	
	/**
	 * 红包类型
	 * single: 单聊红包
	 * group: 群聊红包
	 */
	private String packetType;
	
	/**
	 * 红包类型名称
	 */
	private String packetTypeName;
	
	/**
	 * 群红包类型（仅群聊红包有效）
	 * 1：拼手气 2：普通 3：专属
	 */
	private String type;
	
	/**
	 * 金额
	 */
	private BigDecimal money;
	
	/**
	 * 描述
	 */
	private String info;
	
	/**
	 * 群红包总个数（仅群聊红包有效）
	 */
	private Integer count;
	
	/**
	 * 群红包剩余个数（仅群聊红包有效）
	 */
	private Integer sycount;
	
	/**
	 * 退款时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date tuiTime;
	
	/**
	 * 领取时间（单聊红包）
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date shouTime;
	
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createDate;
	
}

