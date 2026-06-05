package com.seekweb4.chat.modules.assetAdmin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易记录查询DTO
 *
 * @author admin
 * @since 2025-10-28
 */
@Data
public class PaymentTransactionQueryDto {

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
     * 会员ID号（用于通过idno查询）
     */
    private String idno;

    /**
     * 创建时间开始
     */
    private Date createTimeStart;

    /**
     * 创建时间结束
     */
    private Date createTimeEnd;

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 兼容前端传的pageNum参数（前端使用pageNum，后端使用pageNo）
     * 当设置pageNum时，同时设置pageNo
     */
//    public void setPageNum(Integer pageNum) {
//        if (pageNum != null) {
//            this.pageNo = pageNum;
//        }
//    }
}
