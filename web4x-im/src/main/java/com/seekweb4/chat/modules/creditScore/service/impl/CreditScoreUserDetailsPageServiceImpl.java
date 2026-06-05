package com.seekweb4.chat.modules.creditScore.service.impl;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserDetailsPageQueryDto;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreUserDetailsPageMapper;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserDetailsPageService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CreditScoreUserDetailsPageServiceImpl implements CreditScoreUserDetailsPageService {

    @Resource
    private CreditScoreUserDetailsPageMapper creditScoreUserDetailsPageMapper;

    @Override
    public Page<CreditScoreUserDetailsVo> page(CreditScoreUserDetailsPageQueryDto queryDto) {
        int pn = queryDto.getPageNo() == null ? 1 : queryDto.getPageNo();
        int ps = queryDto.getPageSize() == null ? 10 : queryDto.getPageSize();
        Page<CreditScoreUserDetailsVo> page = new Page<>(pn, ps);
        queryDto.setPageNo((pn - 1) * ps);
        queryDto.setPageSize(ps);
        Long count = creditScoreUserDetailsPageMapper.selectCount(queryDto);
        page.setCount(count == null ? 0L : count);
        List<CreditScoreUserDetailsVo> list = creditScoreUserDetailsPageMapper.selectPageList(queryDto);
        page.setList(list);
        return page;
    }

    @Override
    public CreditScoreUserDetailsVo getDetailByUserId(String userId) {
        return creditScoreUserDetailsPageMapper.selectDetailByUserId(userId);
    }
}

