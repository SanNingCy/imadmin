package com.seekweb4.chat.modules.agreement.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.agreement.entity.Agreement;
import com.seekweb4.chat.modules.agreement.mapper.AgreementMapper;

/**
 * 说明协议Service
 * @author lixinapp
 * @version 2021-07-05
 */
@Service
@Transactional(readOnly = true)
public class AgreementService extends CrudService<AgreementMapper, Agreement> {

	public Agreement get(String id) {
		return super.get(id);
	}
	
	public List<Agreement> findList(Agreement agreement) {
		return super.findList(agreement);
	}
	
	public Page<Agreement> findPage(Page<Agreement> page, Agreement agreement) {
		return super.findPage(page, agreement);
	}
	
	@Transactional(readOnly = false)
	public void save(Agreement agreement) {
		super.save(agreement);
	}

	@Transactional(readOnly = false)
	public void update(Agreement agreement) {
		super.update(agreement);
	}
	
	@Transactional(readOnly = false)
	public void delete(Agreement agreement) {
		super.delete(agreement);
	}
	
}