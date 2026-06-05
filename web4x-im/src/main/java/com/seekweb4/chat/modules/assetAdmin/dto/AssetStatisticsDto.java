package com.seekweb4.chat.modules.assetAdmin.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 资产统计DTO
 *
 * @author admin
 * @since 2025-10-28
 */
@Data
public class AssetStatisticsDto {

    /**
     * 总入金金额
     */
    private BigDecimal totalDepositAmount;

    /**
     * 总提现金额
     */
    private BigDecimal totalWithdrawAmount;

    /**
     * 总手续费
     */
    private BigDecimal totalFeeAmount;

    /**
     * 成功入金笔数
     */
    private Long successDepositCount;

    /**
     * 失败入金笔数
     */
    private Long failedDepositCount;

    /**
     * 成功提现笔数
     */
    private Long successWithdrawCount;

    /**
     * 失败提现笔数
     */
    private Long failedWithdrawCount;

    /**
     * 待处理提现笔数
     */
    private Long pendingWithdrawCount;

    /**
     * 今日入金金额
     */
    private BigDecimal todayDepositAmount;

    /**
     * 今日提现金额
     */
    private BigDecimal todayWithdrawAmount;

    /**
     * 今日手续费
     */
    private BigDecimal todayFeeAmount;
}
