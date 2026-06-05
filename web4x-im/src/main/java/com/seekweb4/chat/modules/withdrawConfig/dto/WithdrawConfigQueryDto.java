package com.seekweb4.chat.modules.withdrawConfig.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WithdrawConfigQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private Long id;
    private Integer status;
    private java.math.BigDecimal minAmount;
    private java.math.BigDecimal maxAmount;

    private String orderBy;
}

