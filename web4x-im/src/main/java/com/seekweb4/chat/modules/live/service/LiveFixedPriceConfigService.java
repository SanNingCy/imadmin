package com.seekweb4.chat.modules.live.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.live.dto.LiveFixedPriceConfigQueryDto;
import com.seekweb4.chat.modules.live.dto.LiveFixedPriceConfigVo;
import com.seekweb4.chat.modules.live.entity.LiveFixedPriceConfig;

public interface LiveFixedPriceConfigService {

    Page<LiveFixedPriceConfigVo> page(LiveFixedPriceConfigQueryDto queryDto);

    LiveFixedPriceConfigVo getById(Long id);

    boolean create(LiveFixedPriceConfig config);

    boolean update(LiveFixedPriceConfig config);

    boolean delete(Long id, String updateBy);
}
