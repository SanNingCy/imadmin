package com.seekweb4.chat.modules.rechagelog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.rechagelog.entity.RechageLog;
import com.seekweb4.chat.modules.rechagelog.mapper.RechageLogMapper;

/**
 * 充值记录Service
 * @author lixinapp
 * @version 2024-09-22
 */
@Service
@Transactional(readOnly = true)
public class RechageLogService extends CrudService<RechageLogMapper, RechageLog> {

	public RechageLog get(String id) {
		return super.get(id);
	}
	
	public List<RechageLog> findList(RechageLog rechageLog) {
		return super.findList(rechageLog);
	}
	
	public Page<RechageLog> findPage(Page<RechageLog> page, RechageLog rechageLog) {
		return super.findPage(page, rechageLog);
	}
	
	@Transactional(readOnly = false)
	public void save(RechageLog rechageLog) {
		super.save(rechageLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(RechageLog rechageLog) {
		super.delete(rechageLog);
	}
	
}