package com.seekweb4.chat.modules.upgrade.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.upgrade.entity.Upgrade;
import com.seekweb4.chat.modules.upgrade.mapper.UpgradeMapper;

/**
 * 版本更新Service
 * @author lixinapp
 * @version 2022-12-19
 */
@Service
@Transactional(readOnly = true)
public class UpgradeService extends CrudService<UpgradeMapper, Upgrade> {

	public Upgrade get(String id) {
		return super.get(id);
	}
	
	public List<Upgrade> findList(Upgrade upgrade) {
		return super.findList(upgrade);
	}
	
	public Page<Upgrade> findPage(Page<Upgrade> page, Upgrade upgrade) {
		return super.findPage(page, upgrade);
	}
	
	@Transactional(readOnly = false)
	public void save(Upgrade upgrade) {
		super.save(upgrade);
	}
	
	@Transactional(readOnly = false)
	public void delete(Upgrade upgrade) {
		super.delete(upgrade);
	}
	
}