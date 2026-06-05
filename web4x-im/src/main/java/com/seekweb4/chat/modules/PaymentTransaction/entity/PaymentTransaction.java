package com.seekweb4.chat.modules.PaymentTransaction.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 交易记录表(im入金wx)
 *
 * @author system
 * @since 2025-10-24
 */
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PaymentTransaction implements Serializable {

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
     * 实际总费用金额
     */
    private BigDecimal actualAmount;

    /**
     * 入金金额
     */
    private BigDecimal amount;

    /**
     * 费用金额
     */
    private BigDecimal rateAmount;

    /**
     * 交易状态(1:成功 2:失败)
     */
    private Integer paymentStatus;

    /**
     * 费率信息
     */
    private String rateInfo;

    /**
     * wx付款地址
     */
    private String paymentAddress;

    /**
     * wx实际收款地址
     */
    private String receivingAddress;

    /**
     * 客户的昵称
     */
    private String customerUserName;

    /**
     * 客户的uid
     */
    private String customerUid;

    /**
     * 客户的userId
     */
    private String customerUserId;

    /**
     * 是否内部(0:否 1:是)
     */
    private Integer isInterior;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date  updateTime;

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