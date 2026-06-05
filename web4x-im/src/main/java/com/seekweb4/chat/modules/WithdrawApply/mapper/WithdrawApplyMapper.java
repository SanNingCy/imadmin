package com.seekweb4.chat.modules.WithdrawApply.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawApply;
import com.seekweb4.chat.modules.WithdrawApply.entity.WithdrawPageRequestDTO;
import com.seekweb4.chat.modules.assetAdmin.dto.WithdrawApplyQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author coderpwh
 */
@Mapper
public interface WithdrawApplyMapper extends BaseMapper<WithdrawApply> {

    WithdrawApply selectByWithdrawalId(Long withdrawalId);

    int updateByPrimaryKeySelective(WithdrawApply record);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updateBy") String updateBy);

    int updateStatusAndWithdrawalId(@Param("id") Long id, @Param("status") Integer status,
                                    @Param("withdrawalId") Long withdrawalId, @Param("updateBy") String updateBy);


    /***
     * 根据流水号查询提现申请
     * @param serialNumber
     * @return
     */
    WithdrawApply selectByTransactionNumber(String serialNumber);

    /**
     * 后台管理 - 分页查询提现申请
     * @param queryDto 查询条件
     * @return 提现申请列表
     */
    List<WithdrawApply> selectAdminPageList(WithdrawApplyQueryDto queryDto);

    /**
     * 后台管理 - 统计提现申请总数
     * @param queryDto 查询条件
     * @return 总数
     */
    Long selectAdminCount(WithdrawApplyQueryDto queryDto);

    /**
     * 后台管理 - 统计提现金额
     * @param queryDto 查询条件
     * @return 提现金额统计
     */
    java.math.BigDecimal selectAdminTotalAmount(WithdrawApplyQueryDto queryDto);

    /**
     * 后台管理 - 统计手续费
     * @param queryDto 查询条件
     * @return 手续费统计
     */
    java.math.BigDecimal selectAdminTotalFeeAmount(WithdrawApplyQueryDto queryDto);

    /**
     * 根据主键查询
     * @param id 主键ID
     * @return 提现申请
     */
    WithdrawApply selectByPrimaryKey(Long id);

    /***
     * 分页查询提现申请
     * @param pageRequestDTO
     * @return
     */
    List<WithdrawApply> getWithdrawPage(WithdrawPageRequestDTO pageRequestDTO);


    /***
     * 获取提现申请总数
     * @param pageRequestDTO
     * @return
     */
    Long getWithdrawPageCount(WithdrawPageRequestDTO pageRequestDTO);

    /**
     * 查询待同步的提现记录总数
     *
     * @param statusList 状态列表
     * @return 记录总数
     */
    int countPendingWithdrawals(@Param("statusList") List<Integer> statusList);

    /**
     * 分页查询待同步的提现记录
     *
     * @param statusList 状态列表
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 提现记录列表
     */
    List<WithdrawApply> selectPendingWithdrawals(@Param("statusList") List<Integer> statusList, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 根据状态查询提现申请列表（用于待审核等场景）
     *
     * @param status 状态，如 4-申请中
     * @return 提现申请列表
     */
    List<WithdrawApply> selectListByStatus(@Param("status") Integer status);

}
