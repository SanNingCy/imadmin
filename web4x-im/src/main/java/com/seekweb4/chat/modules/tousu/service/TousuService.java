package com.seekweb4.chat.modules.tousu.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.tousu.entity.Tousu;
import com.seekweb4.chat.modules.tousu.mapper.TousuMapper;

/**
 * 投诉群组记录Service
 * @author lixinapp
 * @version 2024-09-22
 */
@Service
@Transactional(readOnly = true)
public class TousuService extends CrudService<TousuMapper, Tousu> {

	public Tousu get(String id) {
		return super.get(id);
	}
	
	public List<Tousu> findList(Tousu tousu) {
		return super.findList(tousu);
	}
	
	public Page<Tousu> findPage(Page<Tousu> page, Tousu tousu) {
		return super.findPage(page, tousu);
	}
	
	@Transactional(readOnly = false)
	public void save(Tousu tousu) {
		super.save(tousu);
	}
	
	@Transactional(readOnly = false)
	public void delete(Tousu tousu) {
		super.delete(tousu);
	}
	
}