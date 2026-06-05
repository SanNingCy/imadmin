package com.seekweb4.chat.modules.creditScore.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 信用分明细页头部：用户标识与总信用分
 */
@Data
public class CreditScoreLogUserSummaryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String idno;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String lianghao;

    /** t_member_details.credit_score */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private BigDecimal totalCreditScore;
}
