package com.seekweb4.chat.modules.creditScore.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserDetailsPageQueryDto;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsVo;

public interface CreditScoreUserDetailsPageService {

    Page<CreditScoreUserDetailsVo> page(CreditScoreUserDetailsPageQueryDto queryDto);

    CreditScoreUserDetailsVo getDetailByUserId(String userId);
}

