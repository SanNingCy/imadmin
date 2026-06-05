package com.seekweb4.chat.modules.faq.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.faq.entity.Faq;
import com.seekweb4.chat.modules.faq.mapper.FaqMapper;

/**
 * 常见问题Service
 * @author lixinapp
 * @version 2022-12-19
 */
@Service
@Transactional(readOnly = true)
public class FaqService extends CrudService<FaqMapper, Faq> {

	public Faq get(String id) {
		return super.get(id);
	}
	
	public List<Faq> findList(Faq faq) {
		return super.findList(faq);
	}
	
	public Page<Faq> findPage(Page<Faq> page, Faq faq) {
		return super.findPage(page, faq);
	}
	
	@Transactional(readOnly = false)
	public void save(Faq faq) {
		super.save(faq);
	}
	
	@Transactional(readOnly = false)
	public void delete(Faq faq) {
		super.delete(faq);
	}
	
}