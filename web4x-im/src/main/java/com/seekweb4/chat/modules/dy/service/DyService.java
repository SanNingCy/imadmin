package com.seekweb4.chat.modules.dy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.dy.entity.Dy;
import com.seekweb4.chat.modules.dy.mapper.DyMapper;

/**
 * 朋友圈动态Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class DyService extends CrudService<DyMapper, Dy> {

	public Dy get(String id) {
		return super.get(id);
	}
	
	public List<Dy> findList(Dy dy) {
		return super.findList(dy);
	}
	
	public Page<Dy> findPage(Page<Dy> page, Dy dy) {
		return super.findPage(page, dy);
	}
	
	@Transactional(readOnly = false)
	public void save(Dy dy) {
		super.save(dy);
	}

	@Transactional(readOnly = false)
	public void update(Dy dy) {
		super.update(dy);
	}
	
	@Transactional(readOnly = false)
	public void delete(Dy dy) {
		super.delete(dy);
	}
	
}