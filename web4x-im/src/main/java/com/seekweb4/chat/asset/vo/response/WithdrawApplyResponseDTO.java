package com.seekweb4.chat.asset.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author coderpwh
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
public class WithdrawApplyResponseDTO implements Serializable {


    private Long id;

    private String transactionNumber;

    private Integer coinId;

    private BigDecimal actualAmount;

    private BigDecimal amount;

    private BigDecimal rateAmount;

    private String receivingAddress;

    private String withdrawalHash;

    private Integer status;

    private Date createTime;

    private Date updateTime;




}
