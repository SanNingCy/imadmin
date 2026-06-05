package com.seekweb4.chat.modules.tixiantitle.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.tixiantitle.entity.TixianTitle;
import com.seekweb4.chat.modules.tixiantitle.mapper.TixianTitleMapper;

/**
 * 提现页标题Service
 * @author lixinapp
 * @version 2024-09-22
 */
@Service
@Transactional(readOnly = true)
public class TixianTitleService extends CrudService<TixianTitleMapper, TixianTitle> {

	public TixianTitle get(String id) {
		return super.get(id);
	}
	
	public List<TixianTitle> findList(TixianTitle tixianTitle) {
		return super.findList(tixianTitle);
	}
	
	public Page<TixianTitle> findPage(Page<TixianTitle> page, TixianTitle tixianTitle) {
		return super.findPage(page, tixianTitle);
	}
	
	@Transactional(readOnly = false)
	public void save(TixianTitle tixianTitle) {
		super.save(tixianTitle);
	}
	
	@Transactional(readOnly = false)
	public void delete(TixianTitle tixianTitle) {
		super.delete(tixianTitle);
	}
	
}