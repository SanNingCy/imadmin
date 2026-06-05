package com.seekweb4.chat.modules.buttonConfig.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.buttonConfig.entity.ButtonConfig;
import com.seekweb4.chat.modules.buttonConfig.mapper.ButtonConfigMapper;

/**
 * 按钮配置Service
 * @author system
 * @version 2025-11-28
 */
@Service
@Transactional(readOnly = true)
public class ButtonConfigService extends CrudService<ButtonConfigMapper, ButtonConfig> {

	public ButtonConfig get(String id) {
		return super.get(id);
	}
	
	public List<ButtonConfig> findList(ButtonConfig buttonConfig) {
		return super.findList(buttonConfig);
	}
	
	public Page<ButtonConfig> findPage(Page<ButtonConfig> page, ButtonConfig buttonConfig) {
		return super.findPage(page, buttonConfig);
	}
	
	@Transactional(readOnly = false)
	public void save(ButtonConfig buttonConfig) {
		super.save(buttonConfig);
		// 插入后，如果longId被设置，同步到id字段
		if (buttonConfig.getLongId() != null) {
			buttonConfig.getId(); // 触发getId方法，同步id字段
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(ButtonConfig buttonConfig) {
		super.delete(buttonConfig);
	}

	/**
	 * 根据按钮标识查询按钮配置
	 * @param buttonKey 按钮标识
	 * @return 按钮配置
	 */
	public ButtonConfig getByButtonKey(String buttonKey) {
		return mapper.selectByButtonKey(buttonKey);
	}

	@Transactional(readOnly = false)
	public void updateButtonKey(ButtonConfig buttonConfig) {
		mapper.updateKey(buttonConfig);
	}
	
}
