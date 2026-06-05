package com.seekweb4.chat.modules.loginlog.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.seekweb4.chat.modules.loginlog.entity.LoginLog;

import java.util.List;
import java.util.Map;

/**
 * 登录记录MAPPER接口
 * @author lixinapp
 * @version 2024-11-15
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 批量查询最近一次登录成功时间（status=1），按 uid 分组
     */
    List<Map<String, Object>> selectLastSuccessLoginByUids(@Param("uids") List<String> uids);

}
