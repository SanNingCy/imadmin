package com.seekweb4.chat.modules.creditScore.service;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserDetailsPageQueryDto;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsWithTypesVo;

public interface CreditScoreUserDetailsTypePageService {

    Page<CreditScoreUserDetailsWithTypesVo> page(CreditScoreUserDetailsPageQueryDto queryDto);

    /**
     * 单个用户总信用分详情（主表 t_member_details）并附带该用户所有 types[]
     */
    CreditScoreUserDetailsWithTypesVo getDetailWithTypesByUserId(String userId);
}

