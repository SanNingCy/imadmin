package com.seekweb4.chat.modules.creditScore.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreConfig;

public interface CreditScoreConfigService {

    Page<CreditScoreConfig> page(CreditScoreConfigQueryDto queryDto);

    CreditScoreConfig getCurrent();

    CreditScoreConfig getById(Long id);

    boolean save(CreditScoreConfig config);

    boolean update(CreditScoreConfig config);

    /**
     * 逻辑删除
     */
    boolean remove(Long id, String updateBy);
}

