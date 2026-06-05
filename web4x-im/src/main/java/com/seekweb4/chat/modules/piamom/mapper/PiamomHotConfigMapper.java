package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.entity.PiamomHotConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomHotConfigMapper extends BaseMapper<PiamomHotConfig> {

    List<PiamomHotConfig> selectAll();

    PiamomHotConfig selectByPrimaryKey(@Param("id") Long id);

    int insert(PiamomHotConfig record);

    int updateByPrimaryKeySelective(PiamomHotConfig record);

    int deleteByPrimaryKey(@Param("id") Long id);
}
