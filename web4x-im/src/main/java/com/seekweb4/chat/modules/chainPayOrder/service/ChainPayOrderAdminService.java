package com.seekweb4.chat.modules.chainPayOrder.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.chainPayOrder.dto.ChainPayOrderQueryDto;
import com.seekweb4.chat.modules.chainPayOrder.entity.ChainPayOrder;

public interface ChainPayOrderAdminService {

    Page<ChainPayOrder> page(ChainPayOrderQueryDto queryDto);

    ChainPayOrder getById(String id);
}
