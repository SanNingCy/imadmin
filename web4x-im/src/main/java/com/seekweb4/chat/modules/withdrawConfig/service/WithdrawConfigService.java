package com.seekweb4.chat.modules.withdrawConfig.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.withdrawConfig.dto.WithdrawConfigQueryDto;
import com.seekweb4.chat.modules.withdrawConfig.entity.WithdrawConfig;

public interface WithdrawConfigService {

    Page<WithdrawConfig> page(WithdrawConfigQueryDto queryDto);

    WithdrawConfig getById(Long id);

    boolean save(WithdrawConfig config);

    boolean update(WithdrawConfig config);

    boolean remove(Long id);
}

