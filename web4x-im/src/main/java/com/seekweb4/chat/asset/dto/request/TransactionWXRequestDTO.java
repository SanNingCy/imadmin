package com.seekweb4.chat.asset.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */
@Data
public class TransactionWXRequestDTO implements Serializable {


    private String paymentAddress;

    private String receivingAddress;

    private String  amount;

    private String imUserId;

    private String serialNumber;


}
