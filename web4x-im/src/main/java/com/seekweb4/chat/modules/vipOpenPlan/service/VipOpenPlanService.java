package com.seekweb4.chat.modules.vipOpenPlan.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.vipOpenPlan.dto.VipOpenPlanQueryDto;
import com.seekweb4.chat.modules.vipOpenPlan.entity.VipOpenPlan;

import java.util.List;

public interface VipOpenPlanService {

    Page<VipOpenPlan> page(VipOpenPlanQueryDto queryDto);

    VipOpenPlan getById(Long id);

    boolean save(VipOpenPlan plan);

    boolean update(VipOpenPlan plan);

    boolean updateStatus(Long id, Integer status, String updateBy);

    boolean remove(Long id, String updateBy);

    List<VipOpenPlan> listEnabled();
}
