package com.seekweb4.chat.modules.live.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.live.dto.LiveOrderCreateReq;
import com.seekweb4.chat.modules.live.dto.LiveOrderRecordQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveOrderRecord;

public interface LiveOrderService {

    Page<LiveOrderRecord> page(LiveOrderRecordQueryDto queryDto);

    LiveOrderRecord getById(Long id);

    LiveOrderRecord getByOrderNo(String orderNo);

    LiveOrderRecord createOrder(LiveOrderCreateReq req);

    boolean updateStatus(Long id, String liveStatus, String updateBy);

    boolean delete(Long id, String updateBy);
}

