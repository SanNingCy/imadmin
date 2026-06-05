package com.seekweb4.chat.modules.creditScore.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreditScoreUserDetailsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String idno;
    private String lianghao;

    /**
     * t_member_details.credit_score
     */
    private BigDecimal totalCreditScore;

    /**
     * t_member_details.credit_status
     */
    private Integer creditStatus;
}

