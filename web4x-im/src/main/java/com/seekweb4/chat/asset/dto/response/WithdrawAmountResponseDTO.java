package com.seekweb4.chat.asset.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */
@Data
public class WithdrawAmountResponseDTO implements Serializable {

    private BigDecimal actualAmount;


    private BigDecimal amount;

    private BigDecimal rateAmount;


}
