package com.seekweb4.chat.asset.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */
@Data
public class WithdrawalRequestDTO implements Serializable {


    private String userId;

    private String toAddress;

    private String  amount;

    private Integer originType;

    private Integer coinId;

}
