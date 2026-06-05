package com.seekweb4.chat.modules.creditScore.service;

import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsVo;

public interface CreditScoreUserDetailsService {

    CreditScoreUserDetailsVo getDetailsByUserId(String userId);
}

