package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserDetailsPageQueryDto;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserTypeItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CreditScoreUserDetailsTypePageMapper extends BaseMapper<CreditScoreUserDetailsVo> {

    List<CreditScoreUserDetailsVo> selectUserPage(CreditScoreUserDetailsPageQueryDto queryDto);

    Long selectUserCount(CreditScoreUserDetailsPageQueryDto queryDto);

    List<CreditScoreUserTypeItemVo> selectTypeRowsByUserIds(@Param("userIds") List<String> userIds,
                                                            @Param("type") Integer type,
                                                            @Param("subtype") Integer subtype);
}
