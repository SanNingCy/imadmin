package com.seekweb4.chat.modules.loginlog.service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.loginlog.entity.LoginLog;
import com.seekweb4.chat.modules.loginlog.mapper.LoginLogMapper;

/**
 * 登录记录Service
 * @author lixinapp
 * @version 2024-11-15
 */
@Service
@Transactional(readOnly = true)
public class LoginLogService extends CrudService<LoginLogMapper, LoginLog> {

	public LoginLog get(String id) {
		return super.get(id);
	}
	
	public List<LoginLog> findList(LoginLog loginLog) {
		return super.findList(loginLog);
	}
	
	public Page<LoginLog> findPage(Page<LoginLog> page, LoginLog loginLog) {
		return super.findPage(page, loginLog);
	}
	
	@Transactional(readOnly = false)
	public void save(LoginLog loginLog) {
		super.save(loginLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(LoginLog loginLog) {
		super.delete(loginLog);
	}

	/**
	 * 会员 id -> 最近一次登录成功时间
	 */
	public Map<String, Date> getLastSuccessLoginMap(java.util.Collection<String> memberIds) {
		if (memberIds == null || memberIds.isEmpty()) {
			return Collections.emptyMap();
		}
		List<String> ids = new java.util.ArrayList<>(new java.util.LinkedHashSet<>(memberIds));
		if (ids.isEmpty()) {
			return Collections.emptyMap();
		}
		List<Map<String, Object>> rows = mapper.selectLastSuccessLoginByUids(ids);
		Map<String, Date> out = new HashMap<>();
		if (rows == null) {
			return out;
		}
		for (Map<String, Object> row : rows) {
			if (row == null) {
				continue;
			}
			Object u = row.get("uid");
			Object t = row.get("lastLogin");
			if (u == null) {
				continue;
			}
			String uid = u.toString();
			if (t instanceof Date) {
				out.put(uid, (Date) t);
			} else if (t instanceof Timestamp) {
				out.put(uid, new Date(((Timestamp) t).getTime()));
			}
		}
		return out;
	}
	
}