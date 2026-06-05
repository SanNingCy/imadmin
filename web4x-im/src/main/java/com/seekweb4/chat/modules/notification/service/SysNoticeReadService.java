package com.seekweb4.chat.modules.notification.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.notification.entity.SysNoticeRead;
import com.seekweb4.chat.modules.notification.mapper.SysNoticeReadMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SysNoticeReadService extends CrudService<SysNoticeReadMapper, SysNoticeRead> {
	public SysNoticeRead get(String id) { return super.get(id); }
	public List<SysNoticeRead> findList(SysNoticeRead q) { return super.findList(q); }
	public Page<SysNoticeRead> findPage(Page<SysNoticeRead> page, SysNoticeRead q) { return super.findPage(page, q); }
	@Transactional(readOnly = false)
	public void save(SysNoticeRead e) { super.save(e); }
	@Transactional(readOnly = false)
	public void delete(SysNoticeRead e) { super.delete(e); }
}
