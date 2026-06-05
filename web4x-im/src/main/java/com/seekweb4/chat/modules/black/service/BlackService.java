package com.seekweb4.chat.modules.black.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.black.entity.Black;
import com.seekweb4.chat.modules.black.mapper.BlackMapper;

/**
 * 拉黑表Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class BlackService extends CrudService<BlackMapper, Black> {

	public Black get(String id) {
		return super.get(id);
	}
	
	public List<Black> findList(Black black) {
		return super.findList(black);
	}
	
	public Page<Black> findPage(Page<Black> page, Black black) {
		return super.findPage(page, black);
	}
	
	@Transactional(readOnly = false)
	public void save(Black black) {
		super.save(black);
	}

	@Transactional(readOnly = false)
	public void update(Black black) {
		super.update(black);
	}
	
	@Transactional(readOnly = false)
	public void delete(Black black) {
		super.delete(black);
	}
	
}