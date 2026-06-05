package com.seekweb4.chat.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.sys.entity.Post;
import com.seekweb4.chat.modules.sys.mapper.PostMapper;

/**
 * 岗位Service
 */
@Service
@Transactional(readOnly = true)
public class PostService extends CrudService<PostMapper, Post> {

	public Post get(String id) {
		return super.get(id);
	}
	
	public List<Post> findList(Post post) {
		return super.findList(post);
	}
	
	public Page<Post> findPage(Page<Post> page, Post post) {
		return super.findPage(page, post);
	}
	
	@Transactional(readOnly = false)
	public void save(Post post) {
		super.save(post);
	}
	
	@Transactional(readOnly = false)
	public void delete(Post post) {
		super.delete(post);
	}
	
}