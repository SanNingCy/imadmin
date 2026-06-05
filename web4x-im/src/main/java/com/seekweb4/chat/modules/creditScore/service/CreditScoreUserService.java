package com.seekweb4.chat.modules.creditScore.service;

import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserOperateDto;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserOperateResultVo;

public interface CreditScoreUserService {

    CreditScoreUserOperateResultVo addSystem(String userId, Integer subtype, String desc);

    CreditScoreUserOperateResultVo reduceSystem(String userId, Integer subtype, String desc);

    CreditScoreUserOperateResultVo addScore(String userId, Integer type, Integer subtype, String desc);

    CreditScoreUserOperateResultVo reduceScore(String userId, Integer type, Integer subtype, String desc);

    /**
     * 手动输入分数：按输入分数计算（含会员/靓号加成），增加仍按 max_limit 封顶
     */
    CreditScoreUserOperateResultVo addScore(String userId, Integer type, Integer subtype, String desc, java.math.BigDecimal score);

    /**
     * 手动输入分数：按输入分数计算（含会员/靓号加成），允许扣到负数
     */
    CreditScoreUserOperateResultVo reduceScore(String userId, Integer type, Integer subtype, String desc, java.math.BigDecimal score);

    /**
     * 后台开通信用分：按当前基础配置的初始分写入/累加总分，并将 credit_status 置为已开通（1）。
     */
    CreditScoreUserOperateResultVo activateCredit(String userId);
}

