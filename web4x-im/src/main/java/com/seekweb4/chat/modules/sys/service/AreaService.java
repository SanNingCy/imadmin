package com.seekweb4.chat.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.service.TreeService;
import com.seekweb4.chat.modules.sys.entity.Area;
import com.seekweb4.chat.modules.sys.mapper.AreaMapper;
import com.seekweb4.chat.modules.sys.utils.UserUtils;

/**
 * 区域Service
 * @author lixinapp
 * @version 2017-05-16
 */
@Service
@Transactional(readOnly = true)
public class AreaService extends TreeService<AreaMapper, Area> {

	
	public List<Area> findAll(){
		return UserUtils.getAreaList();
	}

	@Transactional(readOnly = false)
	public void save(Area area) {
		super.save(area);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Area area) {
		super.delete(area);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
}
