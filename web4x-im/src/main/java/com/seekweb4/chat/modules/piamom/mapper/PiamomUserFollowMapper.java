package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomFollowQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomUserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomUserFollowMapper extends BaseMapper<PiamomUserFollow> {

    List<PiamomUserFollow> selectAdminPageList(PiamomFollowQueryDto queryDto);

    Long selectAdminCount(PiamomFollowQueryDto queryDto);

    PiamomUserFollow selectByPrimaryKey(@Param("id") Long id);

    int deleteByPrimaryKey(@Param("id") Long id);
}
