package com.seekweb4.chat.modules.assetAdmin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 入金记录查询DTO
 *
 * @author admin
 * @since 2025-10-28
 */
@Data
public class PaymentRecordQueryDto {

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
     * 会员ID号（用于通过idno查询）
     */
    private String idno;


    /**
     * 收款地址
     */
    private String receivingAddress;

    /**
     * 交易状态(1:成功 2:失败)
     */
    private Integer paymentStatus;

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
