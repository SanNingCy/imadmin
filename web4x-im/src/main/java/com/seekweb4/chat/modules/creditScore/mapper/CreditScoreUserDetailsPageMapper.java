package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserDetailsPageQueryDto;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CreditScoreUserDetailsPageMapper extends BaseMapper<CreditScoreUserDetailsVo> {

    List<CreditScoreUserDetailsVo> selectPageList(CreditScoreUserDetailsPageQueryDto queryDto);

    Long selectCount(CreditScoreUserDetailsPageQueryDto queryDto);

    CreditScoreUserDetailsVo selectDetailByUserId(@Param("userId") String userId);
}
