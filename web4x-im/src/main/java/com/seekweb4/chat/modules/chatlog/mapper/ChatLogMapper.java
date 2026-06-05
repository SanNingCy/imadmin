package com.seekweb4.chat.modules.chatlog.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.chatlog.entity.ChatLog;

/**
 * 聊天记录MAPPER接口
 * @author lixinapp
 * @version 2024-09-26
 */
@Mapper
public interface ChatLogMapper extends BaseMapper<ChatLog> {

}
