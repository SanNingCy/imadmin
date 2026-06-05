package com.seekweb4.chat.modules.sys.service;

import com.seekweb4.chat.common.utils.CacheUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.sys.entity.SysConfig;
import com.seekweb4.chat.modules.sys.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统配置Service
 */
@Service
@Transactional(readOnly = true)
public class SysConfigService extends CrudService<SysConfigMapper, SysConfig> {

	public SysConfig get(String id) {
		if(CacheUtils.get("sys-config-v2",id) == null){
			CacheUtils.put("sys-config-v2",id,super.get(id));
		}
		return (SysConfig)CacheUtils.get("sys-config-v2",id);
	}
	
	public List<SysConfig> findList(SysConfig sysConfig) {
		return super.findList(sysConfig);
	}
	
	public Page<SysConfig> findPage(Page<SysConfig> page, SysConfig sysConfig) {
		return super.findPage(page, sysConfig);
	}

	@Transactional(readOnly = false)
	public void save(SysConfig sysConfig) {
		super.save(sysConfig);
		CacheUtils.remove("sys-config-v2", sysConfig.getId());
	}

	@Transactional(readOnly = false)
	public void delete(SysConfig sysConfig) {
		super.delete(sysConfig);
		CacheUtils.remove("sys-config-v2", sysConfig.getId());
	}
	
}