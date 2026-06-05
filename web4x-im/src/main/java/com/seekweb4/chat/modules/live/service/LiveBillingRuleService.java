package com.seekweb4.chat.modules.live.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.live.dto.LiveBillingRuleQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveBillingRule;

import java.math.BigDecimal;

public interface LiveBillingRuleService {

    Page<LiveBillingRule> page(LiveBillingRuleQueryDto queryDto);

    LiveBillingRule getById(Long id);

    boolean create(LiveBillingRule rule);

    boolean update(LiveBillingRule rule);

    boolean delete(Long id, String updateBy);

    LiveBillingRule getActiveRule();

    BigDecimal calcAmount(Integer durationMinutes, Integer tierValue);
}

