package com.seekweb4.chat.modules.signlog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.signlog.entity.SignLog;
import com.seekweb4.chat.modules.signlog.mapper.SignLogMapper;

/**
 * 签到记录Service
 * @author lixinapp
 * @version 2024-09-22
 */
@Service
@Transactional(readOnly = true)
public class SignLogService extends CrudService<SignLogMapper, SignLog> {

	public SignLog get(String id) {
		return super.get(id);
	}
	
	public List<SignLog> findList(SignLog signLog) {
		return super.findList(signLog);
	}
	
	public Page<SignLog> findPage(Page<SignLog> page, SignLog signLog) {
		return super.findPage(page, signLog);
	}
	
	@Transactional(readOnly = false)
	public void save(SignLog signLog) {
		super.save(signLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(SignLog signLog) {
		super.delete(signLog);
	}
	
}