package com.seekweb4.chat.modules.PaymentTransaction.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.PaymentTransaction.entity.PaymentTransaction;
import com.seekweb4.chat.modules.PaymentTransaction.entity.TransactionPageRequestDTO;
import com.seekweb4.chat.modules.assetAdmin.dto.PaymentTransactionQueryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author coderpwh
 */
@Mapper
public interface PaymentTransactionMapper extends BaseMapper<PaymentTransaction> {

    /**
     * 后台管理 - 分页查询交易记录
     *
     * @param queryDto 查询条件
     * @return 交易记录列表
     */
    List<PaymentTransaction> selectAdminPageList(PaymentTransactionQueryDto queryDto);

    /**
     * 后台管理 - 统计交易记录总数
     *
     * @param queryDto 查询条件
     * @return 总数
     */
    Long selectAdminCount(PaymentTransactionQueryDto queryDto);

    /**
     * 后台管理 - 统计交易金额
     *
     * @param queryDto 查询条件
     * @return 交易金额统计
     */
    java.math.BigDecimal selectAdminTotalAmount(PaymentTransactionQueryDto queryDto);

    /**
     * 后台管理 - 统计手续费
     *
     * @param queryDto 查询条件
     * @return 手续费统计
     */
    java.math.BigDecimal selectAdminTotalFeeAmount(PaymentTransactionQueryDto queryDto);

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 交易记录
     */
    PaymentTransaction selectByPrimaryKey(Long id);


    /**
     * 分页查询
     *
     * @param pageRequestDTO 分页参数
     * @return 交易记录列表
     */
    List<PaymentTransaction> getTransactionPage(TransactionPageRequestDTO pageRequestDTO);

    /***
     * 分页查询总数
     * @param param
     * @return
     */
    Long getTransactionPageCount(TransactionPageRequestDTO param);

    int  insertSelective(PaymentTransaction paymentTransaction);

    int  updateByPrimaryKeySelective(PaymentTransaction paymentTransaction);

    int  updateByPrimaryKey(PaymentTransaction paymentTransaction);


    /**
     * 后台管理 - 分页查询交易记录IM内部
     *
     * @param queryDto 查询条件
     * @return 交易记录列表
     */
    List<PaymentTransaction> selectIMAdminPageList(PaymentTransactionQueryDto queryDto);

    /**
     * 后台管理 - 统计交易记录总数IM内部
     *
     * @param queryDto 查询条件
     * @return 总数
     */
    Long selectIMAdminCount(PaymentTransactionQueryDto queryDto);

    /**
     * 根据主键查询IM内部
     *
     * @param id 主键ID
     * @return 交易记录
     */
    PaymentTransaction selectByPrimaryKeyIMByID(Long id);


}
