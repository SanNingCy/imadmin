package com.seekweb4.chat.modules.weburl.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.weburl.entity.Web;
import com.seekweb4.chat.modules.weburl.mapper.WebMapper;

/**
 * 发现页外链Service
 * @author lixinapp
 * @version 2024-09-22
 */
@Service
@Transactional(readOnly = true)
public class WebService extends CrudService<WebMapper, Web> {

	public Web get(String id) {
		return super.get(id);
	}
	
	public List<Web> findList(Web web) {
		return super.findList(web);
	}
	
	public Page<Web> findPage(Page<Web> page, Web web) {
		return super.findPage(page, web);
	}
	
	@Transactional(readOnly = false)
	public void save(Web web) {
		super.save(web);
	}
	
	@Transactional(readOnly = false)
	public void delete(Web web) {
		super.delete(web);
	}
	
}