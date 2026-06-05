package com.seekweb4.chat.modules.paymentRateConfig.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.assetAdmin.dto.PaymentRateConfigQueryDto;
import com.seekweb4.chat.modules.paymentRateConfig.entity.PaymentRateConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author coderpwh
 */
@Mapper
public interface PaymentRateConfigMapper extends BaseMapper<PaymentRateConfig> {


    /***
     *通过支付方式查询
     * @param  paymentType
     * @return
     */
    PaymentRateConfig selectByPaymentType(Integer paymentType);

    /**
     * 后台管理 - 分页查询费率配置
     * @param queryDto 查询条件
     * @return 费率配置列表
     */
    List<PaymentRateConfig> selectAdminPageList(PaymentRateConfigQueryDto queryDto);

    /**
     * 后台管理 - 统计费率配置总数
     * @param queryDto 查询条件
     * @return 总数
     */
    Long selectAdminCount(PaymentRateConfigQueryDto queryDto);

    /**
     * 根据主键查询
     * @param id 主键ID
     * @return 费率配置
     */
    PaymentRateConfig selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新
     * @param record 费率配置
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PaymentRateConfig record);

    /**
     * 插入记录
     * @param record 费率配置
     * @return 影响行数
     */
    int insert(PaymentRateConfig record);

}
