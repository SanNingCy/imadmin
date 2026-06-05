package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.modules.creditScore.entity.CreditScoreMemberDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

@Mapper
public interface CreditScoreMemberDetailsMapper {

    CreditScoreMemberDetails selectByUserId(@Param("userId") String userId);

    int insert(CreditScoreMemberDetails record);

    int updateCreditScoreByUserId(@Param("userId") String userId,
                                    @Param("creditScore") BigDecimal creditScore,
                                    @Param("updateBy") String updateBy,
                                    @Param("updateTime") Date updateTime);

    /**
     * 开通信用分：更新总分与 credit_status
     */
    int updateCreditActivateByUserId(@Param("userId") String userId,
                                     @Param("creditScore") BigDecimal creditScore,
                                     @Param("creditStatus") Integer creditStatus,
                                     @Param("updateBy") String updateBy,
                                     @Param("updateTime") Date updateTime);

    /**
     * 开通信用分：无 t_member_details 行时插入（含 credit_status）
     */
    int insertActivate(CreditScoreMemberDetails record);
}
