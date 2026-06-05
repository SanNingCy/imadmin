package com.seekweb4.chat.modules.tixian.service;

import java.util.List;

import com.seekweb4.chat.modules.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.tixian.entity.Tixian;
import com.seekweb4.chat.modules.tixian.mapper.TixianMapper;

/**
 * 提现申请Service
 * @author lixinapp
 * @version 2024-09-22
 */
@Service
@Transactional(readOnly = true)
public class TixianService extends CrudService<TixianMapper, Tixian> {
	@Autowired
	private MemberService memberService;

	public Tixian get(String id) {
		return super.get(id);
	}
	
	public List<Tixian> findList(Tixian tixian) {
		return super.findList(tixian);
	}
	
	public Page<Tixian> findPage(Page<Tixian> page, Tixian tixian) {
		return super.findPage(page, tixian);
	}
	
	@Transactional(readOnly = false)
	public void save(Tixian tixian) {
		super.save(tixian);
	}
	
	@Transactional(readOnly = false)
	public void delete(Tixian tixian) {
		super.delete(tixian);
	}

	/**
	 * 申请提现
	 * @param tixian
	 */
	@Transactional(readOnly = false)
	public void tixian(Tixian tixian) {
		super.save(tixian);
		memberService.updateBalance(tixian.getU(),tixian.getMoney(),"0","提现申请");
	}
}