package com.seekweb4.chat.modules.friend.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.friend.entity.Friend;

/**
 * 好友关系MAPPER接口
 * @author lixinapp
 * @version 2024-09-20
 */
@Mapper
public interface FriendMapper extends BaseMapper<Friend> {

}
