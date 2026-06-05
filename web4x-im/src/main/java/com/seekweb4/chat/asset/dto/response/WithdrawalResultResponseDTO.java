package com.seekweb4.chat.asset.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class WithdrawalResultResponseDTO implements Serializable {


    private Long withdrawalId;


    private String hash;


    private Integer status;


}
