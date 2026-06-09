package com.seekweb4.chat.modules.live.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.live.dto.LiveUsdtFixedPriceVo;
import com.seekweb4.chat.modules.live.dto.LiveUsdtMeetingPriceBatchSaveReq;
import com.seekweb4.chat.modules.live.dto.LiveUsdtMeetingPriceQueryDto;

import java.math.BigDecimal;

public interface LiveUsdtMeetingPriceConfigService {

    Page<LiveUsdtFixedPriceVo> page(LiveUsdtMeetingPriceQueryDto queryDto);

    LiveUsdtFixedPriceVo getById(Long id);

    boolean batchCreate(LiveUsdtMeetingPriceBatchSaveReq req);

    boolean update(LiveUsdtFixedPriceVo config);

    boolean delete(Long id, String updateBy);

    BigDecimal previewCost(int durationMinutes, int peopleCount);
}
