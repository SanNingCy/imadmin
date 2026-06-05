package com.seekweb4.chat.modules.friend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.friend.entity.Friend;
import com.seekweb4.chat.modules.friend.mapper.FriendMapper;

/**
 * 好友关系Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class FriendService extends CrudService<FriendMapper, Friend> {

	public Friend get(String id) {
		return super.get(id);
	}
	
	public List<Friend> findList(Friend friend) {
		return super.findList(friend);
	}
	
	public Page<Friend> findPage(Page<Friend> page, Friend friend) {
		return super.findPage(page, friend);
	}
	
	@Transactional(readOnly = false)
	public void save(Friend friend) {
		super.save(friend);
	}
	
	@Transactional(readOnly = false)
	public void delete(Friend friend) {
		super.delete(friend);
	}
	
}