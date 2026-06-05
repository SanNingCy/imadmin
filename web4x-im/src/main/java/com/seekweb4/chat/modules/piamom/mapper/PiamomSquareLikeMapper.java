package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.dto.PiamomSquareLikeQueryDto;
import com.seekweb4.chat.modules.piamom.entity.PiamomSquareLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomSquareLikeMapper extends BaseMapper<PiamomSquareLike> {

    List<PiamomSquareLike> selectAdminPageList(PiamomSquareLikeQueryDto queryDto);

    Long selectAdminCount(PiamomSquareLikeQueryDto queryDto);

    PiamomSquareLike selectByPrimaryKey(@Param("id") Long id);

    int deleteByPrimaryKey(@Param("id") Long id);
}
