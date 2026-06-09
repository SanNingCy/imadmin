package com.seekweb4.chat.modules.live.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.live.dto.LiveFixedPriceConfigQueryDto;
import com.seekweb4.chat.modules.live.dto.LiveFixedPriceConfigVo;
import com.seekweb4.chat.modules.live.entity.LiveFixedPriceConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LiveFixedPriceConfigMapper extends BaseMapper<LiveFixedPriceConfig> {

    List<LiveFixedPriceConfigVo> selectAdminPageList(LiveFixedPriceConfigQueryDto queryDto);

    Long selectAdminCount(LiveFixedPriceConfigQueryDto queryDto);

    LiveFixedPriceConfigVo selectByPrimaryKey(@Param("id") Long id);

    int updateByPrimaryKeySelective(LiveFixedPriceConfig record);

    int insert(LiveFixedPriceConfig record);

    long countByDurationAndTier(@Param("durationId") Long durationId,
                                @Param("tierId") Long tierId,
                                @Param("pricingMode") String pricingMode,
                                @Param("excludeId") Long excludeId);
}
