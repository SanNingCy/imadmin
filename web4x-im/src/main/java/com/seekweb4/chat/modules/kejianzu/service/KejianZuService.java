package com.seekweb4.chat.modules.kejianzu.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.kejianzu.entity.KejianZu;
import com.seekweb4.chat.modules.kejianzu.mapper.KejianZuMapper;

/**
 * 课件分组Service
 * @author lixinapp
 * @version 2025-05-24
 */
@Service
@Transactional(readOnly = true)
public class KejianZuService extends CrudService<KejianZuMapper, KejianZu> {

	public KejianZu get(String id) {
		return super.get(id);
	}
	
	public List<KejianZu> findList(KejianZu kejianZu) {
		return super.findList(kejianZu);
	}
	
	public Page<KejianZu> findPage(Page<KejianZu> page, KejianZu kejianZu) {
		return super.findPage(page, kejianZu);
	}
	
	@Transactional(readOnly = false)
	public void save(KejianZu kejianZu) {
		super.save(kejianZu);
	}
	
	@Transactional(readOnly = false)
	public void delete(KejianZu kejianZu) {
		super.delete(kejianZu);
	}
	
}