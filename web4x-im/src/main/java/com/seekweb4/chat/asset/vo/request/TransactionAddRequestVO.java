package com.seekweb4.chat.asset.vo.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */
@Data
public class TransactionAddRequestVO implements Serializable {

    private String receivingAddress;

    private BigDecimal amount;
}
