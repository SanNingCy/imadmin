package com.seekweb4.chat.modules.emoji.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.emoji.entity.Emoji;
import com.seekweb4.chat.modules.emoji.mapper.EmojiMapper;

/**
 * 表情包Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class EmojiService extends CrudService<EmojiMapper, Emoji> {

	public Emoji get(String id) {
		return super.get(id);
	}
	
	public List<Emoji> findList(Emoji emoji) {
		return super.findList(emoji);
	}
	
	public Page<Emoji> findPage(Page<Emoji> page, Emoji emoji) {
		return super.findPage(page, emoji);
	}
	
	@Transactional(readOnly = false)
	public void save(Emoji emoji) {
		super.save(emoji);
	}
	
	@Transactional(readOnly = false)
	public void delete(Emoji emoji) {
		super.delete(emoji);
	}
	
}