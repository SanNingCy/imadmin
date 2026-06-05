package com.seekweb4.chat.modules.creditScore.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreditScoreUserDetailsWithTypesVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String idno;
    private String lianghao;

    private BigDecimal totalCreditScore;
    private Integer creditStatus;

    /**
     * 该用户的多个类型累计分
     */
    private List<CreditScoreUserTypeItemVo> types = new ArrayList<>();
}

