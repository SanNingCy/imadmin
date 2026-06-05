package com.seekweb4.chat.modules.creditScore.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 信用分单条日志详情：日志字段 + 用户 idno/靓号/总信用分
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CreditScoreLogDetailVo extends CreditScoreLog {

    private static final long serialVersionUID = 1L;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String idno;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String lianghao;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private BigDecimal totalCreditScore;
}
