package com.seekweb4.chat.modules.PaymentRecord.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.PaymentRecord.entity.PaymentRecord;
import com.seekweb4.chat.modules.assetAdmin.dto.PaymentRecordQueryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author coderpwh
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    /**
     * 后台管理 - 分页查询入金记录
     * @param queryDto 查询条件
     * @return 入金记录列表
     */
    List<PaymentRecord> selectAdminPageList(PaymentRecordQueryDto queryDto);

    /**
     * 后台管理 - 统计入金记录总数
     * @param queryDto 查询条件
     * @return 总数
     */
    Long selectAdminCount(PaymentRecordQueryDto queryDto);

    /**
     * 后台管理 - 统计入金金额
     * @param queryDto 查询条件
     * @return 入金金额统计
     */
    java.math.BigDecimal selectAdminTotalAmount(PaymentRecordQueryDto queryDto);

    /**
     * 根据主键查询
     * @param id 主键ID
     * @return 入金记录
     */
    PaymentRecord selectByPrimaryKey(Long id);

}
