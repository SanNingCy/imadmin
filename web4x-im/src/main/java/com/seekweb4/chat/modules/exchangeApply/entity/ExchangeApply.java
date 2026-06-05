package com.seekweb4.chat.modules.exchangeApply.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 靓号兑换申请表
 *
 * @author system
 */
@Data
public class ExchangeApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * IM交易号
     */
    private String transactionNumber;

    /**
     * 靓号
     */
    private String prettyNumber;

    /**
     * 链桥凭证
     */
    private String bridgeVoucher;

    /**
     * 链桥用户地址
     */
    private String bridgeUserAddress;

    /**
     * 状态：0-发起兑换，1-兑换中，2-兑换成功，3-兑换失败
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String failReason;

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
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;
}
