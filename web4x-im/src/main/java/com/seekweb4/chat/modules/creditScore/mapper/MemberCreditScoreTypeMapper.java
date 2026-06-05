package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.modules.creditScore.entity.MemberCreditScoreType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface MemberCreditScoreTypeMapper {

    MemberCreditScoreType selectByUserIdTypeSubtype(@Param("userId") String userId,
                                                       @Param("type") Integer type,
                                                       @Param("subtype") Integer subtype);

    int insert(MemberCreditScoreType record);

    int updateCurrentScoreByKeys(@Param("userId") String userId,
                                   @Param("type") Integer type,
                                   @Param("subtype") Integer subtype,
                                   @Param("currentScore") BigDecimal currentScore,
                                   @Param("updateBy") String updateBy,
                                   @Param("updateTime") java.util.Date updateTime);

    /**
     * 计算用户总信用分：sum(t_member_credit_score_type.current_score)
     */
    BigDecimal selectTotalCurrentScoreByUserId(@Param("userId") String userId);
}
