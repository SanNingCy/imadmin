package com.seekweb4.chat.modules.assetAdmin.service.impl;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.PaymentRecord.entity.PaymentRecord;
import com.seekweb4.chat.modules.PaymentTransaction.entity.PaymentTransaction;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawApply;
import com.seekweb4.chat.modules.assetAdmin.dto.*;
import com.seekweb4.chat.modules.paymentRateConfig.entity.PaymentRateConfig;

import java.util.Date;

/**
 * 资产后台管理服务接口
 *
 * @author admin
 * @since 2025-10-28
 */
public interface AssetAdminService {

    /**
     * 分页查询费率配置
     * @param queryDto 查询条件
     * @return 分页结果
     */
    Page<PaymentRateConfig> getPaymentRateConfigPage(PaymentRateConfigQueryDto queryDto);

    /**
     * 根据ID查询费率配置
     * @param id 主键ID
     * @return 费率配置
     */
    PaymentRateConfig getPaymentRateConfigById(Long id);

    /**
     * 根据支付类型查询费率配置
     * @param paymentType 支付类型(1:入金wx 2:提现 3：IM内部)
     * @return 费率配置
     */
    PaymentRateConfig getPaymentRateConfigByPaymentType(Integer paymentType);

    /**
     * 保存费率配置
     * @param paymentRateConfig 费率配置
     * @return 是否成功
     */
    boolean savePaymentRateConfig(PaymentRateConfig paymentRateConfig);

    /**
     * 更新费率配置
     * @param paymentRateConfig 费率配置
     * @return 是否成功
     */
    boolean updatePaymentRateConfig(PaymentRateConfig paymentRateConfig);

    /**
     * 根据类型和ID更新费率配置
     * @param id 主键ID
     * @param paymentType 支付类型(1:入金wx 2:提现 3：IM内部)
     * @param rate 费率
     * @param updateBy 更新人
     * @return 是否成功
     */
    boolean updatePaymentRateConfigByTypeAndId(Long id, Integer paymentType, java.math.BigDecimal rate, String updateBy);

    /**
     * 删除费率配置
     * @param id 主键ID
     * @return 是否成功
     */
    boolean deletePaymentRateConfig(Long id);

    /**
     * 分页查询入金记录
     * @param queryDto 查询条件
     * @return 分页结果
     */
    Page<PaymentRecord> getPaymentRecordPage(PaymentRecordQueryDto queryDto);

    /**
     * 根据ID查询入金记录
     * @param id 主键ID
     * @return 入金记录
     */
    PaymentRecord getPaymentRecordById(Long id);

    /**
     * 分页查询交易记录
     * @param queryDto 查询条件
     * @return 分页结果
     */
    Page<PaymentTransaction> getPaymentTransactionPage(PaymentTransactionQueryDto queryDto);

    /**
     * 根据ID查询交易记录
     * @param id 主键ID
     * @return 交易记录
     */
    PaymentTransaction getPaymentTransactionById(Long id);

    /**
     * 分页查询提现申请
     * @param queryDto 查询条件
     * @return 分页结果
     */
    Page<WithdrawApply> getWithdrawApplyPage(WithdrawApplyQueryDto queryDto);

    /**
     * 根据ID查询提现申请
     * @param id 主键ID
     * @return 提现申请
     */
    WithdrawApply getWithdrawApplyById(Long id);

    /**
     * 根据状态查询提现申请列表（如 status=4 申请中/待审核）
     * @param status 状态
     * @return 提现申请列表
     */
    java.util.List<WithdrawApply> listWithdrawApplyByStatus(Integer status);

    /**
     * 更新提现申请状态
     * @param id 主键ID
     * @param status 状态
     * @param updateBy 更新人
     * @return 是否成功
     */
    boolean updateWithdrawApplyStatus(Long id, Integer status, String updateBy);

    /**
     * 更新提现申请状态和外部提现ID
     * @param id 主键ID
     * @param status 状态
     * @param withdrawalId 外部提现ID
     * @param updateBy 更新人
     * @return 是否成功
     */
    boolean updateWithdrawApplyStatusAndWithdrawalId(Long id, Integer status, Long withdrawalId, String updateBy);

    /**
     * 获取资产统计信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计信息
     */
    AssetStatisticsDto getAssetStatistics(Date startDate, Date endDate);

    /**
     * 获取今日资产统计信息
     * @return 今日统计信息
     */
    AssetStatisticsDto getTodayAssetStatistics();

    /***
     * 提现审核
     * @param id
     * @param status
     * @param remark
     * @param updateBy
     * @return
     */
    Boolean  auditWithdraw(Long id, Integer status, String remark, String updateBy);

    Page<PaymentTransaction> getPaymentTransactionPageIm(PaymentTransactionQueryDto queryDto);

    PaymentTransaction getPaymentTransactionByIdIm(Long id);
}
