package com.seekweb4.chat.modules.changepwdlog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.changepwdlog.entity.ChangePwdLog;
import com.seekweb4.chat.modules.changepwdlog.mapper.ChangePwdLogMapper;

/**
 * 修改登录密码记录Service
 * @author lixinapp
 * @version 2024-11-15
 */
@Service
@Transactional(readOnly = true)
public class ChangePwdLogService extends CrudService<ChangePwdLogMapper, ChangePwdLog> {

	public ChangePwdLog get(String id) {
		return super.get(id);
	}
	
	public List<ChangePwdLog> findList(ChangePwdLog changePwdLog) {
		return super.findList(changePwdLog);
	}
	
	public Page<ChangePwdLog> findPage(Page<ChangePwdLog> page, ChangePwdLog changePwdLog) {
		return super.findPage(page, changePwdLog);
	}
	
	@Transactional(readOnly = false)
	public void save(ChangePwdLog changePwdLog) {
		super.save(changePwdLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChangePwdLog changePwdLog) {
		super.delete(changePwdLog);
	}
	
}