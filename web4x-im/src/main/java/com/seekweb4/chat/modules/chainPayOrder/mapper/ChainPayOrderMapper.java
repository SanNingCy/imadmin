package com.seekweb4.chat.modules.chainPayOrder.mapper;

import com.seekweb4.chat.modules.chainPayOrder.dto.ChainPayOrderQueryDto;
import com.seekweb4.chat.modules.chainPayOrder.entity.ChainPayOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChainPayOrderMapper {

    List<ChainPayOrder> selectAdminPageList(ChainPayOrderQueryDto queryDto);

    Long selectAdminCount(ChainPayOrderQueryDto queryDto);

    ChainPayOrder selectByPrimaryKey(@Param("id") String id);
}
