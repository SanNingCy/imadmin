package com.seekweb4.chat.modules.chatlog.entity;

import com.seekweb4.chat.modules.member.entity.Member;
import com.seekweb4.chat.modules.group.entity.Group;
import java.util.Date;

import com.seekweb4.chat.core.persistence.DataEntity;
import com.seekweb4.chat.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 聊天记录Entity
 * @author lixinapp
 * @version 2024-09-26
 */
@Data
public class ChatLog extends DataEntity<ChatLog> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="类型 1：单聊  2：群聊", dictType="shouzhi", align=2, sort=1)
	private String type;		// 类型 1：单聊  2：群聊
	@ExcelField(title="发送者", fieldType=Member.class, value="u.idno", align=2, sort=2)
	private Member u;		// 发送者
	@ExcelField(title="群（群聊）", fieldType=Group.class, value="group.name", align=2, sort=3)
	private Group group;		// 群（群聊）
	@ExcelField(title="接收者（单聊）", fieldType=Member.class, value="uid2.idno", align=2, sort=4)
	private Member uid2;		// 接收者（单聊）
	@ExcelField(title="随机数", align=2, sort=5)
	private String random;		// 随机数
	@ExcelField(title="消息序列号", align=2, sort=6)
	private String msgSeq;		// 消息序列号
	@ExcelField(title="时间", align=2, sort=7)
	private String msgTime;		// 时间
	@ExcelField(title="消息体", align=2, sort=8)
	private String msgBody;		// 消息体
	@ExcelField(title="消息类型", align=2, sort=9)
	private String msgType;		// 消息类型
	@ExcelField(title="消息内容", align=2, sort=10)
	private String msgContend;		// 消息内容
	private Date beginCreateDate;		// 开始 添加时间
	private Date endCreateDate;		// 结束 添加时间
	
	public ChatLog() {
		super();
	}
	
	public ChatLog(String id){
		super(id);
	}
}