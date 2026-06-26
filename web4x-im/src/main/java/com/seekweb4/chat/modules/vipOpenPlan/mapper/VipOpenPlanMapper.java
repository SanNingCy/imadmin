package com.seekweb4.chat.modules.vipOpenPlan.mapper;

import com.seekweb4.chat.modules.vipOpenPlan.dto.VipOpenPlanQueryDto;
import com.seekweb4.chat.modules.vipOpenPlan.entity.VipOpenPlan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VipOpenPlanMapper {

    VipOpenPlan selectByPrimaryKey(@Param("id") Long id);

    int insert(VipOpenPlan record);

    int updateByPrimaryKeySelective(VipOpenPlan record);

    List<VipOpenPlan> selectAdminPageList(VipOpenPlanQueryDto queryDto);

    Long selectAdminCount(VipOpenPlanQueryDto queryDto);

    List<VipOpenPlan> selectAllEnabled();

    /** 未逻辑删除的套餐数量 */
    Long countActive();
}
