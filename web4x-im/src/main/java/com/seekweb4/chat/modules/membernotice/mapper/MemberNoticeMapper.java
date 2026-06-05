package com.seekweb4.chat.modules.membernotice.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.seekweb4.chat.modules.membernotice.entity.MemberNotice;

/**
 * 用户系统消息MAPPER接口
 * @author lixinapp
 * @version 2024-12-23
 */
@Mapper
public interface MemberNoticeMapper extends BaseMapper<MemberNotice> {

}
