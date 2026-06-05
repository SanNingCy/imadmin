package com.seekweb4.chat.modules.express.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.express.entity.Express;
import com.seekweb4.chat.modules.express.mapper.ExpressMapper;

/**
 * 物流公司Service
 * @author lixinapp
 * @version 2023-02-06
 */
@Service
@Transactional(readOnly = true)
public class ExpressService extends CrudService<ExpressMapper, Express> {

	public Express get(String id) {
		return super.get(id);
	}
	
	public List<Express> findList(Express express) {
		return super.findList(express);
	}
	
	public Page<Express> findPage(Page<Express> page, Express express) {
		return super.findPage(page, express);
	}
	
	@Transactional(readOnly = false)
	public void save(Express express) {
		super.save(express);
	}
	
	@Transactional(readOnly = false)
	public void delete(Express express) {
		super.delete(express);
	}
	
}