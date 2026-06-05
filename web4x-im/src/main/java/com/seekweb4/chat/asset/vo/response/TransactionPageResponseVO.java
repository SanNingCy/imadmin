package com.seekweb4.chat.asset.vo.response;

import jnr.ffi.annotations.In;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author coderpwh
 */
@Data
public class TransactionPageResponseVO implements Serializable {


    private Long id;

    private String userId;

    private String transactionNumber;

    private BigDecimal amount;

    private BigDecimal actualAmount;

    private BigDecimal rateAmount;

    private Integer paymentStatus;

    private Date createTime;

    private Date updateTime;

    private String receivingAddress;


}
