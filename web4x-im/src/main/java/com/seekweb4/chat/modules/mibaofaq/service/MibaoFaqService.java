package com.seekweb4.chat.modules.mibaofaq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.mibaofaq.entity.MibaoFaq;
import com.seekweb4.chat.modules.mibaofaq.mapper.MibaoFaqMapper;

/**
 * 密保问题Service
 * @author lixinapp
 * @version 2024-09-20
 */
@Service
@Transactional(readOnly = true)
public class MibaoFaqService extends CrudService<MibaoFaqMapper, MibaoFaq> {

	public MibaoFaq get(String id) {
		return super.get(id);
	}
	
	public List<MibaoFaq> findList(MibaoFaq mibaoFaq) {
		return super.findList(mibaoFaq);
	}
	
	public Page<MibaoFaq> findPage(Page<MibaoFaq> page, MibaoFaq mibaoFaq) {
		return super.findPage(page, mibaoFaq);
	}
	
	@Transactional(readOnly = false)
	public void save(MibaoFaq mibaoFaq) {
		super.save(mibaoFaq);
	}
	
	@Transactional(readOnly = false)
	public void delete(MibaoFaq mibaoFaq) {
		super.delete(mibaoFaq);
	}
	
}