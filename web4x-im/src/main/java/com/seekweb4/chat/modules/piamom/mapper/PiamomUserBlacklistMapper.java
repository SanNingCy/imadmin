package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomBlacklistQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomUserBlacklist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomUserBlacklistMapper extends BaseMapper<PiamomUserBlacklist> {

    List<PiamomUserBlacklist> selectAdminPageList(PiamomBlacklistQueryDto queryDto);

    Long selectAdminCount(PiamomBlacklistQueryDto queryDto);

    PiamomUserBlacklist selectByPrimaryKey(@Param("id") Long id);

    int deleteByPrimaryKey(@Param("id") Long id);
}
