package com.seekweb4.chat.modules.creditScore.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreditScoreUserOperateResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private Integer type;
    private Integer subtype;

    /**
     * 本次实际变更分数（增加为正，扣减为负）
     */
    private BigDecimal deltaScore;

    /**
     * 变更后：该 type/subtype 对应的累计分
     */
    private BigDecimal currentScore;

    /**
     * 变更后：用户总信用分（t_member_details.credit_score）
     */
    private BigDecimal totalCreditScore;
}

