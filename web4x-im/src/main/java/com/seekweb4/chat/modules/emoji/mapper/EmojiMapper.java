package com.seekweb4.chat.modules.emoji.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.emoji.entity.Emoji;

/**
 * 表情包MAPPER接口
 * @author lixinapp
 * @version 2024-09-20
 */
@Mapper
public interface EmojiMapper extends BaseMapper<Emoji> {

}
