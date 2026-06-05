package com.seekweb4.chat.modules.dylike.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.dylike.entity.DyLike;

/**
 * 动态点赞MAPPER接口
 * @author lixinapp
 * @version 2024-09-20
 */
@Mapper
public interface DyLikeMapper extends BaseMapper<DyLike> {

}
