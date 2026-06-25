package com.seekweb4.chat.modules.chainPayOrder.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 链上支付订单 t_chain_pay_order
 */
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ChainPayOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String orderId;
    private String userId;
    private String scene;
    private Integer chainId;
    private BigDecimal amount;
    /** USDT amount 按汇率换算后的 ODIC 金额（下单快照，非实时换算） */
    private BigDecimal odicAmount;
    private String rawAmount;
    private String tokenSymbol;
    private String tokenAddress;
    private Integer paymentType;
    private String bizPayload;
    private String txHash;
    private String userAddress;
    /** 0待支付 1已支付 2业务完成 3失败 4过期 */
    private Integer status;
    /** 0未对账 1已对账 */
    private Integer reconcileStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completeTime;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 关联会员 ID 号（非表字段） */
    private String idno;
    /** 关联会员昵称（非表字段） */
    private String nickname;
}
