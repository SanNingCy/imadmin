package com.seekweb4.chat.modules.piamom.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.piamom.entity.PiamomReportConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PiamomReportConfigMapper extends BaseMapper<PiamomReportConfig> {

    List<PiamomReportConfig> selectAll();

    PiamomReportConfig selectByPrimaryKey(@Param("id") Long id);

    int insert(PiamomReportConfig record);

    int updateByPrimaryKeySelective(PiamomReportConfig record);

    int deleteByPrimaryKey(@Param("id") Long id);
}
