package com.seekweb4.chat.modules.xcx.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.xcx.entity.Xcx;
import com.seekweb4.chat.modules.xcx.mapper.XcxMapper;

/**
 * 小程序链接管理Service
 * @author lixinapp
 * @version 2024-09-22
 */
@Service
@Transactional(readOnly = true)
public class XcxService extends CrudService<XcxMapper, Xcx> {

	public Xcx get(String id) {
		return super.get(id);
	}
	
	public List<Xcx> findList(Xcx xcx) {
		return super.findList(xcx);
	}
	
	public Page<Xcx> findPage(Page<Xcx> page, Xcx xcx) {
		return super.findPage(page, xcx);
	}
	
	@Transactional(readOnly = false)
	public void save(Xcx xcx) {
		super.save(xcx);
	}
	
	@Transactional(readOnly = false)
	public void delete(Xcx xcx) {
		super.delete(xcx);
	}
	
}