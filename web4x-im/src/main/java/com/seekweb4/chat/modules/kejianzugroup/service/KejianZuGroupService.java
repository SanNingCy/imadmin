package com.seekweb4.chat.modules.kejianzugroup.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.kejianzugroup.entity.KejianZuGroup;
import com.seekweb4.chat.modules.kejianzugroup.mapper.KejianZuGroupMapper;

/**
 * 课件分组群组关联Service
 * @author lixinapp
 * @version 2025-05-24
 */
@Service
@Transactional(readOnly = true)
public class KejianZuGroupService extends CrudService<KejianZuGroupMapper, KejianZuGroup> {

	public KejianZuGroup get(String id) {
		return super.get(id);
	}
	
	public List<KejianZuGroup> findList(KejianZuGroup kejianZuGroup) {
		return super.findList(kejianZuGroup);
	}
	
	public Page<KejianZuGroup> findPage(Page<KejianZuGroup> page, KejianZuGroup kejianZuGroup) {
		return super.findPage(page, kejianZuGroup);
	}
	
	@Transactional(readOnly = false)
	public void save(KejianZuGroup kejianZuGroup) {
		super.save(kejianZuGroup);
	}
	
	@Transactional(readOnly = false)
	public void delete(KejianZuGroup kejianZuGroup) {
		super.delete(kejianZuGroup);
	}
	
}