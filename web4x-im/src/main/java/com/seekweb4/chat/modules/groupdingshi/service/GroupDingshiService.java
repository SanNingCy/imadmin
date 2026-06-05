package com.seekweb4.chat.modules.groupdingshi.service;

import java.util.List;

import com.seekweb4.chat.delayedQueue.GroupHuanyingDelayedQueueListener;
import com.seekweb4.chat.delayedQueue.RedisDelayedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.groupdingshi.entity.GroupDingshi;
import com.seekweb4.chat.modules.groupdingshi.mapper.GroupDingshiMapper;

/**
 * 群定时消息Service
 * @author lixinapp
 * @version 2025-03-24
 */
@Service
@Transactional(readOnly = true)
public class GroupDingshiService extends CrudService<GroupDingshiMapper, GroupDingshi> {
	@Autowired
	private RedisDelayedQueue redisDelayedQueue;


	public GroupDingshi get(String id) {
		return super.get(id);
	}
	
	public List<GroupDingshi> findList(GroupDingshi groupDingshi) {
		return super.findList(groupDingshi);
	}
	
	public Page<GroupDingshi> findPage(Page<GroupDingshi> page, GroupDingshi groupDingshi) {
		return super.findPage(page, groupDingshi);
	}
	
	@Transactional(readOnly = false)
	public void save(GroupDingshi groupDingshi) {
		super.save(groupDingshi);
	}
	
	@Transactional(readOnly = false)
	public void delete(GroupDingshi groupDingshi) {
		super.delete(groupDingshi);
	}

	public void addDingshi(String id){
		redisDelayedQueue.addQueueHours(id,24, GroupHuanyingDelayedQueueListener.class);
	}

	
}