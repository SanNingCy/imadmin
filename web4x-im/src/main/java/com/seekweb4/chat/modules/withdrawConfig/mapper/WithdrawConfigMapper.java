package com.seekweb4.chat.modules.withdrawConfig.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.withdrawConfig.dto.WithdrawConfigQueryDto;
import com.seekweb4.chat.modules.withdrawConfig.entity.WithdrawConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WithdrawConfigMapper extends BaseMapper<WithdrawConfig> {

    WithdrawConfig selectByPrimaryKey(Long id);

    int insert(WithdrawConfig record);

    int updateByPrimaryKeySelective(WithdrawConfig record);

    int deleteByPrimaryKey(Long id);

    List<WithdrawConfig> selectAdminPageList(WithdrawConfigQueryDto queryDto);

    Long selectAdminCount(WithdrawConfigQueryDto queryDto);
}
