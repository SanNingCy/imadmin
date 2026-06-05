package com.seekweb4.chat.modules.changephonelog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.changephonelog.entity.ChangePhoneLog;
import com.seekweb4.chat.modules.changephonelog.mapper.ChangePhoneLogMapper;

/**
 * 修改手机号记录Service
 * @author lixinapp
 * @version 2024-11-15
 */
@Service
@Transactional(readOnly = true)
public class ChangePhoneLogService extends CrudService<ChangePhoneLogMapper, ChangePhoneLog> {

	public ChangePhoneLog get(String id) {
		return super.get(id);
	}
	
	public List<ChangePhoneLog> findList(ChangePhoneLog changePhoneLog) {
		return super.findList(changePhoneLog);
	}
	
	public Page<ChangePhoneLog> findPage(Page<ChangePhoneLog> page, ChangePhoneLog changePhoneLog) {
		return super.findPage(page, changePhoneLog);
	}
	
	@Transactional(readOnly = false)
	public void save(ChangePhoneLog changePhoneLog) {
		super.save(changePhoneLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChangePhoneLog changePhoneLog) {
		super.delete(changePhoneLog);
	}
	
}