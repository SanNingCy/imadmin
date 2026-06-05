package com.seekweb4.chat.modules.notification.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.notification.entity.SysNotification;
import com.seekweb4.chat.modules.notification.mapper.SysNotificationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SysNotificationService extends CrudService<SysNotificationMapper, SysNotification> {
	public SysNotification get(String id) { return super.get(id); }
	public List<SysNotification> findList(SysNotification q) { return super.findList(q); }
	public Page<SysNotification> findPage(Page<SysNotification> page, SysNotification q) { return super.findPage(page, q); }
	@Transactional(readOnly = false)
	public void save(SysNotification e) { super.save(e); }
	@Transactional(readOnly = false)
	public void delete(SysNotification e) { super.delete(e); }
}
