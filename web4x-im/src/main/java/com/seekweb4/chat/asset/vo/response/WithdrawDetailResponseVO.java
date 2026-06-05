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
public class WithdrawDetailResponseVO implements Serializable {


    private String userId;

    private String transactionNumber;

    private Integer coinId;

    private BigDecimal amount;

    private BigDecimal actualAmount;

    private BigDecimal rateAmount;

    private Integer status;

    private String receivingAddress;

    private String withdrawalHash;

    private Date createTime;

    private Date updateTime;

    private String remark;


}
