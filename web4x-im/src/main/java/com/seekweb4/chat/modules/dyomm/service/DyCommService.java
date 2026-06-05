package com.seekweb4.chat.modules.dyomm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.dyomm.entity.DyComm;
import com.seekweb4.chat.modules.dyomm.mapper.DyCommMapper;

/**
 * 动态评论Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class DyCommService extends CrudService<DyCommMapper, DyComm> {

	public DyComm get(String id) {
		return super.get(id);
	}
	
	public List<DyComm> findList(DyComm dyComm) {
		return super.findList(dyComm);
	}
	
	public Page<DyComm> findPage(Page<DyComm> page, DyComm dyComm) {
		return super.findPage(page, dyComm);
	}
	
	@Transactional(readOnly = false)
	public void save(DyComm dyComm) {
		super.save(dyComm);
	}

	@Transactional(readOnly = false)
	public void update(DyComm dyComm) {
		super.update(dyComm);
	}
	
	@Transactional(readOnly = false)
	public void delete(DyComm dyComm) {
		super.delete(dyComm);
	}
	
}