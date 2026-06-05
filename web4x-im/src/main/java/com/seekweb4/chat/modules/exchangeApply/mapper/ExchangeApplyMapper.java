package com.seekweb4.chat.modules.exchangeApply.mapper;

import com.seekweb4.chat.modules.assetAdmin.dto.ExchangeApplyQueryDto;
import com.seekweb4.chat.modules.exchangeApply.entity.ExchangeApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 靓号兑换申请表 Mapper（仅查询，不提供新增）
 *
 * @author system
 */
@Mapper
public interface ExchangeApplyMapper {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 靓号兑换申请
     */
    ExchangeApply selectByPrimaryKey(@Param("id") Long id);

    /**
     * 后台管理 - 分页查询靓号兑换申请
     *
     * @param queryDto 查询条件
     * @return 申请列表
     */
    List<ExchangeApply> selectAdminPageList(ExchangeApplyQueryDto queryDto);

    /**
     * 后台管理 - 统计靓号兑换申请总数
     *
     * @param queryDto 查询条件
     * @return 总数
     */
    Long selectAdminCount(ExchangeApplyQueryDto queryDto);
}
