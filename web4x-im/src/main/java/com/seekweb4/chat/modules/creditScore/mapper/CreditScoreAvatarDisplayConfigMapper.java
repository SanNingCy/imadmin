package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreAvatarDisplayConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreAvatarDisplayConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CreditScoreAvatarDisplayConfigMapper extends BaseMapper<CreditScoreAvatarDisplayConfig> {

    CreditScoreAvatarDisplayConfig selectByPrimaryKey(Long id);

    CreditScoreAvatarDisplayConfig selectCurrent();

    int insert(CreditScoreAvatarDisplayConfig record);

    int updateByPrimaryKeySelective(CreditScoreAvatarDisplayConfig record);

    int deleteByPrimaryKey(Long id);

    List<CreditScoreAvatarDisplayConfig> selectAdminPageList(CreditScoreAvatarDisplayConfigQueryDto queryDto);

    Long selectAdminCount(CreditScoreAvatarDisplayConfigQueryDto queryDto);
}
