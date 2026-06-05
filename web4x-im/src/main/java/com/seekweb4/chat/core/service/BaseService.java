package com.seekweb4.chat.core.service;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.BaseEntity;
import com.seekweb4.chat.modules.sys.entity.DataRule;
import com.seekweb4.chat.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service基类
 * @author lixinapp
 * @version 2017-05-16
 */
@Transactional(readOnly = true)
public abstract class BaseService {
	
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	
	/**
	 * 数据范围过滤
	 * @param entity 当前过滤的实体类
	 */
	public static void dataRuleFilter(BaseEntity<?> entity) {
		if(UserUtils.getUser() == null ||  StringUtils.isBlank(UserUtils.getUser().getId())){
			return;
		}
		entity.setCurrentUser(UserUtils.getUser());
		List<DataRule> dataRuleList = UserUtils.getDataRuleList();
		
		// 如果是超级管理员，则不过滤数据
		if (dataRuleList.size() == 0) {
			return;
		}

		// 数据范围
		StringBuilder sqlString = new StringBuilder();

		
			for(DataRule dataRule : dataRuleList){
				if(entity.getClass().getSimpleName().equals(dataRule.getClassName())){
					sqlString.append(dataRule.getDataScopeSql());
				}
				
			}

		entity.setDataScope(sqlString.toString());
		
	}

}
