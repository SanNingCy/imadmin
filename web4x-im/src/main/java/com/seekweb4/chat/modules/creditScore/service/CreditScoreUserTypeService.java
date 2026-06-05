package com.seekweb4.chat.modules.creditScore.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserTypePageQueryDto;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserTypeVo;

public interface CreditScoreUserTypeService {

    Page<CreditScoreUserTypeVo> page(CreditScoreUserTypePageQueryDto queryDto);

    CreditScoreUserTypeVo getDetailById(Long id);
}

