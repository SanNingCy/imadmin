package com.seekweb4.chat.modules.changenamelog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.changenamelog.entity.ChangeNameLog;
import com.seekweb4.chat.modules.changenamelog.mapper.ChangeNameLogMapper;

/**
 * 修改昵称记录Service
 * @author lixinapp
 * @version 2024-11-15
 */
@Service
@Transactional(readOnly = true)
public class ChangeNameLogService extends CrudService<ChangeNameLogMapper, ChangeNameLog> {

	public ChangeNameLog get(String id) {
		return super.get(id);
	}
	
	public List<ChangeNameLog> findList(ChangeNameLog changeNameLog) {
		return super.findList(changeNameLog);
	}
	
	public Page<ChangeNameLog> findPage(Page<ChangeNameLog> page, ChangeNameLog changeNameLog) {
		return super.findPage(page, changeNameLog);
	}
	
	@Transactional(readOnly = false)
	public void save(ChangeNameLog changeNameLog) {
		super.save(changeNameLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChangeNameLog changeNameLog) {
		super.delete(changeNameLog);
	}
	
}