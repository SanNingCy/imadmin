package com.seekweb4.chat.modules.dylike.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.dylike.entity.DyLike;
import com.seekweb4.chat.modules.dylike.mapper.DyLikeMapper;

/**
 * 动态点赞Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class DyLikeService extends CrudService<DyLikeMapper, DyLike> {

	public DyLike get(String id) {
		return super.get(id);
	}
	
	public List<DyLike> findList(DyLike dyLike) {
		return super.findList(dyLike);
	}
	
	public Page<DyLike> findPage(Page<DyLike> page, DyLike dyLike) {
		return super.findPage(page, dyLike);
	}
	
	@Transactional(readOnly = false)
	public void save(DyLike dyLike) {
		super.save(dyLike);
	}

	@Transactional(readOnly = false)
	public void update(DyLike dyLike) {
		super.update(dyLike);
	}
	
	@Transactional(readOnly = false)
	public void delete(DyLike dyLike) {
		super.delete(dyLike);
	}
	
}