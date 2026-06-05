package com.seekweb4.chat.asset.vo.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author coderpwh
 */
@Data
public class TransactionRequestVO implements Serializable {


    @NotBlank(message = "合作方单号不能为空")
    private String partnerNumber;

    @NotNull(message = "金额不能为空")
    private String amount;

    @NotBlank(message = "用户id不能为空")
    private String userId;

    @NotBlank(message = "收款地址不能为空")
    private String receivingAddress;


}
