package com.seekweb4.chat.modules.faq.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.faq.entity.Faq;

/**
 * 常见问题MAPPER接口
 * @author lixinapp
 * @version 2022-12-19
 */
@Mapper
public interface FaqMapper extends BaseMapper<Faq> {

}
