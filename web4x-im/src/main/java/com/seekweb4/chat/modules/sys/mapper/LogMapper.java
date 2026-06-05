package com.seekweb4.chat.modules.sys.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.sys.entity.Log;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志MAPPER接口
 * @author lixinapp
 * @version 2017-05-16
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {

	public void empty();
}
