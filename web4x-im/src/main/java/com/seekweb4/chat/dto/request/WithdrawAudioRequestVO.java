package com.seekweb4.chat.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */

@Data
public class WithdrawAudioRequestVO  implements Serializable {
    private String receivingAddress;

    private BigDecimal amount;

    private BigDecimal originAmount;


}
