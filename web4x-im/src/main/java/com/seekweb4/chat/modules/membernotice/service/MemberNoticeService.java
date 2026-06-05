package com.seekweb4.chat.modules.membernotice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.membernotice.entity.MemberNotice;
import com.seekweb4.chat.modules.membernotice.mapper.MemberNoticeMapper;

/**
 * 用户系统消息Service
 * @author lixinapp
 * @version 2024-12-23
 */
@Service
@Transactional(readOnly = true)
public class MemberNoticeService extends CrudService<MemberNoticeMapper, MemberNotice> {

	public MemberNotice get(String id) {
		return super.get(id);
	}
	
	public List<MemberNotice> findList(MemberNotice memberNotice) {
		return super.findList(memberNotice);
	}
	
	public Page<MemberNotice> findPage(Page<MemberNotice> page, MemberNotice memberNotice) {
		return super.findPage(page, memberNotice);
	}
	
	@Transactional(readOnly = false)
	public void save(MemberNotice memberNotice) {
		super.save(memberNotice);
	}
	
	@Transactional(readOnly = false)
	public void delete(MemberNotice memberNotice) {
		super.delete(memberNotice);
	}
	
}