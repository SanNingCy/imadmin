package com.seekweb4.chat.modules.coll.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.coll.entity.Coll;
import com.seekweb4.chat.modules.coll.mapper.CollMapper;

/**
 * 收藏记录Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class CollService extends CrudService<CollMapper, Coll> {

	public Coll get(String id) {
		return super.get(id);
	}
	
	public List<Coll> findList(Coll coll) {
		return super.findList(coll);
	}
	
	public Page<Coll> findPage(Page<Coll> page, Coll coll) {
		return super.findPage(page, coll);
	}
	
	@Transactional(readOnly = false)
	public void save(Coll coll) {
		super.save(coll);
	}

	@Transactional(readOnly = false)
	public void update(Coll coll) {
		super.update(coll);
	}
	
	@Transactional(readOnly = false)
	public void delete(Coll coll) {
		super.delete(coll);
	}
	
}