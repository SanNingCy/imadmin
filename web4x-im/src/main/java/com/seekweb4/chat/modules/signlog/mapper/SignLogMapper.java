package com.seekweb4.chat.modules.signlog.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.signlog.entity.SignLog;

/**
 * 签到记录MAPPER接口
 * @author lixinapp
 * @version 2024-09-22
 */
@Mapper
public interface SignLogMapper extends BaseMapper<SignLog> {

}
