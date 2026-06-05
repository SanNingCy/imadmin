package com.seekweb4.chat.modules.notice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.notice.entity.Notice;
import com.seekweb4.chat.modules.notice.mapper.NoticeMapper;

/**
 * 系统通知Service
 * @author lixinapp
 * @version 2024-12-23
 */
@Service
@Transactional(readOnly = true)
public class NoticeService extends CrudService<NoticeMapper, Notice> {

	public Notice get(String id) {
		return super.get(id);
	}
	
	public List<Notice> findList(Notice notice) {
		return super.findList(notice);
	}
	
	public Page<Notice> findPage(Page<Notice> page, Notice notice) {
		return super.findPage(page, notice);
	}
	
	@Transactional(readOnly = false)
	public void save(Notice notice) {
		super.save(notice);
	}
	
	@Transactional(readOnly = false)
	public void delete(Notice notice) {
		super.delete(notice);
	}
	
}