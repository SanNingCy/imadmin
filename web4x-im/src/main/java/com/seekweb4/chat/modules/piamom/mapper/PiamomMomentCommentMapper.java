package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomCommentQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomMomentComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomMomentCommentMapper extends BaseMapper<PiamomMomentComment> {

    List<PiamomMomentComment> selectAdminPageList(PiamomCommentQueryDto queryDto);

    Long selectAdminCount(PiamomCommentQueryDto queryDto);

    PiamomMomentComment selectByPrimaryKey(@Param("id") Long id);

    int deleteByPrimaryKey(@Param("id") Long id);
}
