package com.seekweb4.chat.modules.sys.mapper;

import java.util.List;

import com.seekweb4.chat.core.persistence.TreeMapper;
import com.seekweb4.chat.modules.sys.entity.Office;

import org.apache.ibatis.annotations.Mapper;

/**
 * 机构MAPPER接口
 * @author lixinapp
 * @version 2017-05-16
 */
@Mapper
public interface OfficeMapper extends TreeMapper<Office> {
	
	public Office getByCode(String code);

}
