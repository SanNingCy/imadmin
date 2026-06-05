package com.seekweb4.chat.modules.reason.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.reason.entity.Reason;
import com.seekweb4.chat.modules.reason.mapper.ReasonMapper;

/**
 * 投诉原因Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class ReasonService extends CrudService<ReasonMapper, Reason> {

	public Reason get(String id) {
		return super.get(id);
	}
	
	public List<Reason> findList(Reason reason) {
		return super.findList(reason);
	}
	
	public Page<Reason> findPage(Page<Reason> page, Reason reason) {
		return super.findPage(page, reason);
	}
	
	@Transactional(readOnly = false)
	public void save(Reason reason) {
		super.save(reason);
	}
	
	@Transactional(readOnly = false)
	public void delete(Reason reason) {
		super.delete(reason);
	}
	
}