package com.seekweb4.chat.modules.changepaypwdlog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.changepaypwdlog.entity.ChangePaypwdLog;
import com.seekweb4.chat.modules.changepaypwdlog.mapper.ChangePaypwdLogMapper;

/**
 * 修改支付密码记录Service
 * @author lixinapp
 * @version 2024-11-15
 */
@Service
@Transactional(readOnly = true)
public class ChangePaypwdLogService extends CrudService<ChangePaypwdLogMapper, ChangePaypwdLog> {

	public ChangePaypwdLog get(String id) {
		return super.get(id);
	}
	
	public List<ChangePaypwdLog> findList(ChangePaypwdLog changePaypwdLog) {
		return super.findList(changePaypwdLog);
	}
	
	public Page<ChangePaypwdLog> findPage(Page<ChangePaypwdLog> page, ChangePaypwdLog changePaypwdLog) {
		return super.findPage(page, changePaypwdLog);
	}
	
	@Transactional(readOnly = false)
	public void save(ChangePaypwdLog changePaypwdLog) {
		super.save(changePaypwdLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChangePaypwdLog changePaypwdLog) {
		super.delete(changePaypwdLog);
	}
	
}