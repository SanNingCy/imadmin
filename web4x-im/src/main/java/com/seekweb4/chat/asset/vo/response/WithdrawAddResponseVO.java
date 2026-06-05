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
public class WithdrawAddResponseVO implements Serializable {


    private String transactionNumber;

    private BigDecimal actualAmount;

    private BigDecimal amount;

    private BigDecimal rateAmount;

    private Integer status;

    private String receivingAddress;

    private String withdrawalHash;

    private Date createTime;

    private Date updateTime;

    private Long withdrawalId;

}
