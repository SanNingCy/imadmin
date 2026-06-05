package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreTypeConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreTypeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CreditScoreTypeConfigMapper extends BaseMapper<CreditScoreTypeConfig> {

    CreditScoreTypeConfig selectByPrimaryKey(Long id);

    int insert(CreditScoreTypeConfig record);

    int updateByPrimaryKeySelective(CreditScoreTypeConfig record);

    int deleteByPrimaryKey(Long id);

    List<CreditScoreTypeConfig> selectAdminPageList(CreditScoreTypeConfigQueryDto queryDto);

    Long selectAdminCount(CreditScoreTypeConfigQueryDto queryDto);

    /**
     * 查询全部类型（is_deleted=0）
     */
    List<CreditScoreTypeConfig> selectAllEnabledAndDisabled();

    /**
     * 查询启用类型（is_deleted=0 and status=1）
     */
    List<CreditScoreTypeConfig> selectAllEnabled();

    /**
     * 用于用户加减分：仅查询未删除且状态启用的数据。
     */
    CreditScoreTypeConfig selectEnabledByTypeSubtype(@Param("type") Integer type, @Param("subtype") Integer subtype);
}
