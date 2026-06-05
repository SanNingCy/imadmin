package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomNotifyQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomNotifyMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomNotifyMessageMapper extends BaseMapper<PiamomNotifyMessage> {

    List<PiamomNotifyMessage> selectAdminPageList(PiamomNotifyQueryDto queryDto);

    Long selectAdminCount(PiamomNotifyQueryDto queryDto);

    PiamomNotifyMessage selectByPrimaryKey(@Param("id") Long id);
}
