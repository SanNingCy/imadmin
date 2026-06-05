package com.seekweb4.chat.modules.quhao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.quhao.entity.Quhao;
import com.seekweb4.chat.modules.quhao.mapper.QuhaoMapper;

/**
 * 手机区号Service
 * @author lixinapp
 * @version 2024-09-24
 */
@Service
@Transactional(readOnly = true)
public class QuhaoService extends CrudService<QuhaoMapper, Quhao> {

	public Quhao get(String id) {
		return super.get(id);
	}
	
	public List<Quhao> findList(Quhao quhao) {
		return super.findList(quhao);
	}
	
	public Page<Quhao> findPage(Page<Quhao> page, Quhao quhao) {
		return super.findPage(page, quhao);
	}
	
	@Transactional(readOnly = false)
	public void save(Quhao quhao) {
		super.save(quhao);
	}
	
	@Transactional(readOnly = false)
	public void delete(Quhao quhao) {
		super.delete(quhao);
	}
	
}