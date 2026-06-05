package com.seekweb4.chat.modules.grouphongbaolog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.grouphongbaolog.entity.GroupHongbaoLog;
import com.seekweb4.chat.modules.grouphongbaolog.mapper.GroupHongbaoLogMapper;

/**
 * 群红包领取记录Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class GroupHongbaoLogService extends CrudService<GroupHongbaoLogMapper, GroupHongbaoLog> {

	public GroupHongbaoLog get(String id) {
		return super.get(id);
	}
	
	public List<GroupHongbaoLog> findList(GroupHongbaoLog groupHongbaoLog) {
		return super.findList(groupHongbaoLog);
	}
	
	public Page<GroupHongbaoLog> findPage(Page<GroupHongbaoLog> page, GroupHongbaoLog groupHongbaoLog) {
		return super.findPage(page, groupHongbaoLog);
	}
	
	@Transactional(readOnly = false)
	public void save(GroupHongbaoLog groupHongbaoLog) {
		super.save(groupHongbaoLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(GroupHongbaoLog groupHongbaoLog) {
		super.delete(groupHongbaoLog);
	}
	
}