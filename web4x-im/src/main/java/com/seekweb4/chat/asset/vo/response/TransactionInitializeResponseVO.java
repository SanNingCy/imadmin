package com.seekweb4.chat.asset.vo.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */
@Data
public class TransactionInitializeResponseVO implements Serializable {


    /**
     * 账户余额
     */
    private BigDecimal balances;

    /**
     * 积分余额
     */
    private BigDecimal pointTotal;


    private String paymentAddress;


}
