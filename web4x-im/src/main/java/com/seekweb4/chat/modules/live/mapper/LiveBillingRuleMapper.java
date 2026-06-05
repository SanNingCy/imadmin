package com.seekweb4.chat.modules.live.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.live.dto.LiveBillingRuleQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveBillingRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LiveBillingRuleMapper extends BaseMapper<LiveBillingRule> {

    List<LiveBillingRule> selectAdminPageList(LiveBillingRuleQueryDto queryDto);

    Long selectAdminCount(LiveBillingRuleQueryDto queryDto);

    LiveBillingRule selectByPrimaryKey(@Param("id") Long id);

    int updateByPrimaryKeySelective(LiveBillingRule record);

    int insert(LiveBillingRule record);

    /** 获取当前启用的计费规则（取最新一条） */
    LiveBillingRule selectActiveOne();
}
