package com.seekweb4.chat.asset.vo.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */
@Data
public class TransactionAddResponseVO implements Serializable {

    private String transactionNumber;

    private BigDecimal actualAmount;

    private BigDecimal amount;

    private BigDecimal rateAmount;

    private Integer paymentStatus;

    private String receivingAddress;

    private String errorMessage;




}
