package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomProfileStatQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomUserProfileStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomUserProfileStatMapper extends BaseMapper<PiamomUserProfileStat> {

    List<PiamomUserProfileStat> selectAdminPageList(PiamomProfileStatQueryDto queryDto);

    Long selectAdminCount(PiamomProfileStatQueryDto queryDto);

    PiamomUserProfileStat selectByPrimaryKey(@Param("id") Long id);

    PiamomUserProfileStat selectByUserId(@Param("userId") String userId);
}
