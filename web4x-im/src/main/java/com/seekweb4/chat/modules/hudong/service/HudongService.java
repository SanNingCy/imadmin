package com.seekweb4.chat.modules.hudong.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.hudong.entity.Hudong;
import com.seekweb4.chat.modules.hudong.mapper.HudongMapper;

/**
 * 互动消息Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class HudongService extends CrudService<HudongMapper, Hudong> {

	public Hudong get(String id) {
		return super.get(id);
	}
	
	public List<Hudong> findList(Hudong hudong) {
		return super.findList(hudong);
	}
	
	public Page<Hudong> findPage(Page<Hudong> page, Hudong hudong) {
		return super.findPage(page, hudong);
	}
	
	@Transactional(readOnly = false)
	public void save(Hudong hudong) {
		super.save(hudong);
	}
	
	@Transactional(readOnly = false)
	public void delete(Hudong hudong) {
		super.delete(hudong);
	}
	
}