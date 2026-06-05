package com.seekweb4.chat.modules.live.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.live.dto.LiveConfigSelectOptionVo;
import com.seekweb4.chat.modules.live.dto.LiveTimeDurationConfigQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveTimeDurationConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LiveTimeDurationConfigMapper extends BaseMapper<LiveTimeDurationConfig> {

    List<LiveTimeDurationConfig> selectAdminPageList(LiveTimeDurationConfigQueryDto queryDto);

    Long selectAdminCount(LiveTimeDurationConfigQueryDto queryDto);

    LiveTimeDurationConfig selectByPrimaryKey(@Param("id") Long id);

    int updateByPrimaryKeySelective(LiveTimeDurationConfig record);

    int insert(LiveTimeDurationConfig record);

    List<LiveTimeDurationConfig> listEnabled();

    /** 未删除的全部时长，用于后台下拉（含禁用） */
    List<LiveConfigSelectOptionVo> listSelectOptions();
}
