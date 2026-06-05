package com.seekweb4.chat.modules.notice.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.notice.entity.Notice;

/**
 * 系统通知MAPPER接口
 * @author lixinapp
 * @version 2024-12-23
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

}
