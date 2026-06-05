package com.seekweb4.chat.asset.vo.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author coderpwh
 */
@Data
public class TransactionDetailResponseVO implements Serializable {

    private String transactionNumber;

    private BigDecimal actualAmount;

    private BigDecimal amount;

    private BigDecimal rateAmount;


    private Integer paymentStatus;


    private Date createTime;

    private Date updateTime;

    private String receivingAddress;

    private String nickName;


}
