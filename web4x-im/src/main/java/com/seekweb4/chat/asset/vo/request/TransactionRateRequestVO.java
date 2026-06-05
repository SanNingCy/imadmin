package com.seekweb4.chat.asset.vo.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */
@Data
public class TransactionRateRequestVO implements Serializable {


    private BigDecimal amount;


    /***
     * 1:入金 2:提现
     */
    private Integer type;


}
