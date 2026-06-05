package com.seekweb4.chat.modules.chatlog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.chatlog.entity.ChatLog;
import com.seekweb4.chat.modules.chatlog.mapper.ChatLogMapper;

/**
 * 聊天记录Service
 * @author lixinapp
 * @version 2024-09-26
 */
@Service
@Transactional(readOnly = true)
public class ChatLogService extends CrudService<ChatLogMapper, ChatLog> {

	public ChatLog get(String id) {
		return super.get(id);
	}
	
	public List<ChatLog> findList(ChatLog chatLog) {
		return super.findList(chatLog);
	}
	
	public Page<ChatLog> findPage(Page<ChatLog> page, ChatLog chatLog) {
		return super.findPage(page, chatLog);
	}
	
	@Transactional(readOnly = false)
	public void save(ChatLog chatLog) {
		super.save(chatLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChatLog chatLog) {
		super.delete(chatLog);
	}
	
}