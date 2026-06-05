package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.modules.creditScore.vo.CreditScoreMemberInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface CreditScoreMemberMapper {

    CreditScoreMemberInfoVo selectMemberInfoByUserId(@Param("userId") String userId);

    /**
     * t_member.create_date，用于信用分头像角标新用户判定
     */
    Date selectMemberCreateDateByUserId(@Param("userId") String userId);
}
