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

    /**
     * 按时长、人数档位 ID 解析 USDT 售价（启用配置），未配置时抛异常。
     */
    BigDecimal resolveSalePriceUsdt(Long durationId, Long tierId);

    /**
     * 按时长分钟数、人数上限解析 USDT 售价，未配置时返回 null。
     */
    BigDecimal findSalePriceUsdtByValue(Integer durationMinutes, Integer tierValue);

    /**
     * 按时长、人数档位 ID 解析 USDT 售价，未配置时返回 null。
     */
    BigDecimal findSalePriceUsdt(Long durationId, Long tierId);
}
