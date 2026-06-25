package com.seekweb4.chat.modules.chainPayOrder.dto;

import lombok.Data;

import java.util.Date;

/**
 * 链上支付订单后台查询 DTO
 */
@Data
public class ChainPayOrderQueryDto {

    private String id;
    private String orderId;
    private String userId;
    private String idno;
    private String scene;
    private Integer chainId;
    private String tokenSymbol;
    private Integer paymentType;
    private String txHash;
    private String userAddress;
    private Integer status;
    private Integer reconcileStatus;
    private Date createTimeStart;
    private Date createTimeEnd;

    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private String orderBy;
}
