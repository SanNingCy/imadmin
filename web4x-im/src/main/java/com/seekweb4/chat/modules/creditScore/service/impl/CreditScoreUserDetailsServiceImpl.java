package com.seekweb4.chat.modules.creditScore.service.impl;

import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreMemberDetailsMapper;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreMemberMapper;
import com.seekweb4.chat.modules.creditScore.mapper.MemberCreditScoreTypeMapper;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreMemberDetails;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserDetailsService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreMemberInfoVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CreditScoreUserDetailsServiceImpl implements CreditScoreUserDetailsService {

    @Resource
    private com.seekweb4.chat.modules.creditScore.mapper.CreditScoreMemberMapper creditScoreMemberMapper;

    @Resource
    private com.seekweb4.chat.modules.creditScore.mapper.CreditScoreMemberDetailsMapper creditScoreMemberDetailsMapper;

    @Resource
    private com.seekweb4.chat.modules.creditScore.mapper.MemberCreditScoreTypeMapper memberCreditScoreTypeMapper;

    @Override
    public CreditScoreUserDetailsVo getDetailsByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }

        CreditScoreMemberInfoVo member = creditScoreMemberMapper.selectMemberInfoByUserId(userId);
        if (member == null) {
            return null;
        }

        BigDecimal total;
        Integer creditStatus = null;
        CreditScoreMemberDetails details = creditScoreMemberDetailsMapper.selectByUserId(userId);
        if (details != null && details.getCreditScore() != null) {
            total = details.getCreditScore();
            creditStatus = details.getCreditStatus();
        } else {
            total = memberCreditScoreTypeMapper.selectTotalCurrentScoreByUserId(userId);
        }

        CreditScoreUserDetailsVo vo = new CreditScoreUserDetailsVo();
        vo.setUserId(userId);
        vo.setIdno(member.getIdno());
        vo.setLianghao(member.getLianghao());
        vo.setTotalCreditScore(total);
        vo.setCreditStatus(creditStatus);
        return vo;
    }
}

