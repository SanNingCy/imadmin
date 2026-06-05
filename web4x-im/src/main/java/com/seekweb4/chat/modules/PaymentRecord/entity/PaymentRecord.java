package com.seekweb4.chat.modules.PaymentRecord.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 入金记录表(wx入金Im)
 *
 * @author system
 * @since 2025-10-24
 */
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PaymentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 交易流水号
     */
    private String transactionNumber;

    /**
     * 合作方单号(wx方)
     */
    private String partnerNumber;

    /**
     * 入金金额
     */
    private BigDecimal amount;

    /**
     * 付款地址
     */
    private String paymentAddress;

    /**
     * 收款地址
     */
    private String receivingAddress;

    /**
     * 交易状态(1:成功 2:失败)
     */
    private Integer paymentStatus;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 创建人id
     */
    private String createBy;

    /**
     * 更新人id
     */
    private String updateBy;

    /**
     * 是否删除(0:否 1:是)
     */
    private Integer isDeleted;

    /**
     * 会员昵称
     */
    private String nickname;

    /**
     * 会员ID号
     */
    private String idno;
}