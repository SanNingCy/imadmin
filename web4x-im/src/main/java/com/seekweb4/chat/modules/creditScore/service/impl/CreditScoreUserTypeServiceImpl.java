package com.seekweb4.chat.modules.creditScore.service.impl;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserTypePageQueryDto;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreUserTypeMapper;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserTypeService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CreditScoreUserTypeServiceImpl implements CreditScoreUserTypeService {

    @Resource
    private CreditScoreUserTypeMapper creditScoreUserTypeMapper;

    @Override
    public Page<CreditScoreUserTypeVo> page(CreditScoreUserTypePageQueryDto queryDto) {
        int pn = queryDto.getPageNo() == null ? 1 : queryDto.getPageNo();
        int ps = queryDto.getPageSize() == null ? 10 : queryDto.getPageSize();
        Page<CreditScoreUserTypeVo> page = new Page<>(pn, ps);
        queryDto.setPageNo((pn - 1) * ps);
        queryDto.setPageSize(ps);
        Long count = creditScoreUserTypeMapper.selectCount(queryDto);
        page.setCount(count == null ? 0L : count);
        List<CreditScoreUserTypeVo> list = creditScoreUserTypeMapper.selectPageList(queryDto);
        page.setList(list);
        return page;
    }

    @Override
    public CreditScoreUserTypeVo getDetailById(Long id) {
        return creditScoreUserTypeMapper.selectDetailById(id);
    }
}

