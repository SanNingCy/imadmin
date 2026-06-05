package com.seekweb4.chat.modules.creditScore.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreTypeConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreTypeConfig;
import java.util.List;

public interface CreditScoreTypeConfigService {

    Page<CreditScoreTypeConfig> page(CreditScoreTypeConfigQueryDto queryDto);

    CreditScoreTypeConfig getById(Long id);

    boolean save(CreditScoreTypeConfig config);

    boolean update(CreditScoreTypeConfig config);

    boolean updateStatus(Long id, Integer status, String updateBy);

    /**
     * 逻辑删除
     */
    boolean remove(Long id, String updateBy);

    /**
     * 用于用户加减分：仅查询未删除且状态启用的数据。
     */
    CreditScoreTypeConfig getEnabledByTypeSubtype(Integer type, Integer subtype);

    /**
     * 获取全部类型（包含启用/禁用，均为未删除）
     */
    List<CreditScoreTypeConfig> listAllTypes();

    /**
     * 获取启用类型
     */
    List<CreditScoreTypeConfig> listEnabledTypes();
}

