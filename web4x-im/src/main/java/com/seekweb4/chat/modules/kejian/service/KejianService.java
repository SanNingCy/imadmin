package com.seekweb4.chat.modules.kejian.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.kejian.entity.Kejian;
import com.seekweb4.chat.modules.kejian.mapper.KejianMapper;

/**
 * 课件Service
 * @author lixinapp
 * @version 2025-05-24
 */
@Service
@Transactional(readOnly = true)
public class KejianService extends CrudService<KejianMapper, Kejian> {

	public Kejian get(String id) {
		return super.get(id);
	}
	
	public List<Kejian> findList(Kejian kejian) {
		return super.findList(kejian);
	}
	
	public Page<Kejian> findPage(Page<Kejian> page, Kejian kejian) {
		return super.findPage(page, kejian);
	}
	
	@Transactional(readOnly = false)
	public void save(Kejian kejian) {
		super.save(kejian);
	}
	
	@Transactional(readOnly = false)
	public void delete(Kejian kejian) {
		super.delete(kejian);
	}
	
}