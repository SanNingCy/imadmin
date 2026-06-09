package com.seekweb4.chat.modules.live.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.live.dto.LiveConfigSelectOptionVo;
import com.seekweb4.chat.modules.live.dto.LiveUserTierConfigQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveUserTierConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LiveUserTierConfigMapper extends BaseMapper<LiveUserTierConfig> {

    List<LiveUserTierConfig> selectAdminPageList(LiveUserTierConfigQueryDto queryDto);

    Long selectAdminCount(LiveUserTierConfigQueryDto queryDto);

    LiveUserTierConfig selectByPrimaryKey(@Param("id") Long id);

    int updateByPrimaryKeySelective(LiveUserTierConfig record);

    int insert(LiveUserTierConfig record);

    List<LiveUserTierConfig> listEnabled();

    /** 未删除的全部人数档位，用于后台下拉（含禁用） */
    List<LiveConfigSelectOptionVo> listSelectOptions();

    LiveUserTierConfig selectByTierValue(@Param("tierValue") Integer tierValue);
}
