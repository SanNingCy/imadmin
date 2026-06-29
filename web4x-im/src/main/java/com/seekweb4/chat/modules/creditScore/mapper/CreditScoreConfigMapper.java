package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CreditScoreConfigMapper extends BaseMapper<CreditScoreConfig> {

    CreditScoreConfig selectByPrimaryKey(Long id);

    CreditScoreConfig selectByPrimaryKeyLegacy(Long id);

    CreditScoreConfig selectCurrent();

    CreditScoreConfig selectCurrentLegacy();

    int insert(CreditScoreConfig record);

    int updateByPrimaryKeySelective(CreditScoreConfig record);

    int deleteByPrimaryKey(Long id);

    List<CreditScoreConfig> selectAdminPageList(CreditScoreConfigQueryDto queryDto);

    List<CreditScoreConfig> selectAdminPageListLegacy(CreditScoreConfigQueryDto queryDto);

    Long selectAdminCount(CreditScoreConfigQueryDto queryDto);
}
