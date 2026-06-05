package com.seekweb4.chat.asset.vo.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */
@Data
public class TransactionRateResponseVO implements Serializable {


    private BigDecimal amount;

    private BigDecimal actualAmount;

    private BigDecimal rateAmount;

    private BigDecimal rateInfo;

}
