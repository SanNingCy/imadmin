package com.seekweb4.chat.modules.groupuplog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.groupuplog.entity.GroupUplog;
import com.seekweb4.chat.modules.groupuplog.mapper.GroupUplogMapper;

/**
 * 群升级记录Service
 * @author lixinapp
 * @version 2025-03-24
 */
@Service
@Transactional(readOnly = true)
public class GroupUplogService extends CrudService<GroupUplogMapper, GroupUplog> {

	public GroupUplog get(String id) {
		return super.get(id);
	}
	
	public List<GroupUplog> findList(GroupUplog groupUplog) {
		return super.findList(groupUplog);
	}
	
	public Page<GroupUplog> findPage(Page<GroupUplog> page, GroupUplog groupUplog) {
		return super.findPage(page, groupUplog);
	}
	
	@Transactional(readOnly = false)
	public void save(GroupUplog groupUplog) {
		super.save(groupUplog);
	}
	
	@Transactional(readOnly = false)
	public void delete(GroupUplog groupUplog) {
		super.delete(groupUplog);
	}
	
}