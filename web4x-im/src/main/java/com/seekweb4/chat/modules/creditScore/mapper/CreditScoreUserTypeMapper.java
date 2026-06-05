package com.seekweb4.chat.modules.creditScore.mapper;

import com.seekweb4.chat.core.persistence.BaseMapper;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserTypePageQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreMemberDetails;
import com.seekweb4.chat.modules.creditScore.entity.MemberCreditScoreType;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserTypeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CreditScoreUserTypeMapper extends BaseMapper<MemberCreditScoreType> {

    List<CreditScoreUserTypeVo> selectPageList(CreditScoreUserTypePageQueryDto queryDto);

    Long selectCount(CreditScoreUserTypePageQueryDto queryDto);

    CreditScoreUserTypeVo selectDetailById(@Param("id") Long id);
}
