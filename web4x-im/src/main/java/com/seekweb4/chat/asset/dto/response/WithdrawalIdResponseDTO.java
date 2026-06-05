package com.seekweb4.chat.asset.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class WithdrawalIdResponseDTO implements Serializable {


    private Long withdrawalId;

    private String hash;


    private Integer status;

}
