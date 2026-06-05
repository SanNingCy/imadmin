package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomMomentLikeQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomMomentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomMomentLikeMapper extends BaseMapper<PiamomMomentLike> {

    List<PiamomMomentLike> selectAdminPageList(PiamomMomentLikeQueryDto queryDto);

    Long selectAdminCount(PiamomMomentLikeQueryDto queryDto);

    PiamomMomentLike selectByPrimaryKey(@Param("id") Long id);

    int deleteByPrimaryKey(@Param("id") Long id);
}
