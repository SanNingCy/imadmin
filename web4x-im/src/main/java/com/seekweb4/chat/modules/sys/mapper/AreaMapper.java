package com.seekweb4.chat.modules.sys.mapper;

import com.seekweb4.chat.core.persistence.TreeMapper;
import com.seekweb4.chat.modules.sys.entity.Area;
import org.apache.ibatis.annotations.Mapper;

/**
 * 区域MAPPER接口
 * @author lixinapp
 * @version 2017-05-16
 */
@Mapper
public interface AreaMapper extends TreeMapper<Area> {
	
}
