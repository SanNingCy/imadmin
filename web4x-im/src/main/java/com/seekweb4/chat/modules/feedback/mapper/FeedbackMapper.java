package com.seekweb4.chat.modules.feedback.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.feedback.entity.Feedback;

/**
 * 意见反馈MAPPER接口
 * @author lixinapp
 * @version 2023-08-30
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

}
