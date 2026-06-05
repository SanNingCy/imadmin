package com.seekweb4.chat.modules.sign.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.sign.entity.Sign;
import com.seekweb4.chat.modules.sign.mapper.SignMapper;

/**
 * 签到奖励配置Service
 * @author lixinapp
 * @version 2024-09-22
 */
@Service
@Transactional(readOnly = true)
public class SignService extends CrudService<SignMapper, Sign> {

	public Sign get(String id) {
		return super.get(id);
	}
	
	public List<Sign> findList(Sign sign) {
		return super.findList(sign);
	}
	
	public Page<Sign> findPage(Page<Sign> page, Sign sign) {
		return super.findPage(page, sign);
	}
	
	@Transactional(readOnly = false)
	public void save(Sign sign) {
		super.save(sign);
	}
	
	@Transactional(readOnly = false)
	public void delete(Sign sign) {
		super.delete(sign);
	}
	
}