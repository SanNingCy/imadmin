package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomUserLikeRecordQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomUserLikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomUserLikeRecordMapper extends BaseMapper<PiamomUserLikeRecord> {

    List<PiamomUserLikeRecord> selectAdminPageList(PiamomUserLikeRecordQueryDto queryDto);

    Long selectAdminCount(PiamomUserLikeRecordQueryDto queryDto);

    PiamomUserLikeRecord selectByPrimaryKey(@Param("id") Long id);

    int deleteByPrimaryKey(@Param("id") Long id);
}
