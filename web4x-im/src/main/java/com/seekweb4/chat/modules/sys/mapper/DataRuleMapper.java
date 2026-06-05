package com.seekweb4.chat.modules.sys.mapper;

import java.util.List;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.sys.entity.DataRule;
import com.seekweb4.chat.modules.sys.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据权限MAPPER接口
 * @author lgf
 * @version 2017-04-02
 */
@Mapper
public interface DataRuleMapper extends BaseMapper<DataRule> {

	public void deleteRoleDataRule(DataRule dataRule);
	
	public List<DataRule> findByUserId(User user);
}
