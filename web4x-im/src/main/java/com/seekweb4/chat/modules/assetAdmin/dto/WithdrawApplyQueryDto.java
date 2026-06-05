package com.seekweb4.chat.modules.assetAdmin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现申请查询DTO
 *
 * @author admin
 * @since 2025-10-28
 */
@Data
public class WithdrawApplyQueryDto {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 提现交易号
     */
    private String transactionNumber;

    /**
     * 种类(1:积分 2:代币)
     */
    private Integer coinId;

    /**
     * 实际提现金额
     */
    private BigDecimal actualAmount;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 费率金额
     */
    private BigDecimal rateAmount;

    /**
     * 提现id(外部)
     */
    private Long withdrawalId;

    /**
     * 提现状态(0:发起提现 1:正在提现 2:提现成功 3:提现失败)
     */
    private Integer status;

    /**
     * 提现金额下限（查询 amount >= amountMin 的记录，如 10000 表示大额提现）
     */
    private BigDecimal amountMin;

    /**
     * 收款地址
     */
    private String receivingAddress;

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
