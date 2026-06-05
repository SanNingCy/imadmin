package com.seekweb4.chat.modules.exchangeApply.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.assetAdmin.dto.ExchangeApplyQueryDto;
import com.seekweb4.chat.modules.exchangeApply.entity.ExchangeApply;

/**
 * 靓号兑换申请 Service（仅查询，不提供新增）
 *
 * @author system
 */
public interface ExchangeApplyService {

    /**
     * 分页查询靓号兑换申请
     *
     * @param queryDto 查询条件
     * @return 分页结果
     */
    Page<ExchangeApply> getPage(ExchangeApplyQueryDto queryDto);

    /**
     * 根据ID查询靓号兑换申请
     *
     * @param id 主键ID
     * @return 靓号兑换申请
     */
    ExchangeApply getById(Long id);
}
