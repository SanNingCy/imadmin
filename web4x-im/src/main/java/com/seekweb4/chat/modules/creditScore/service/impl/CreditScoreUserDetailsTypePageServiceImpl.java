package com.seekweb4.chat.modules.creditScore.service.impl;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreUserDetailsPageQueryDto;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreUserDetailsTypePageMapper;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreUserDetailsTypePageService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserDetailsWithTypesVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreUserTypeItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CreditScoreUserDetailsTypePageServiceImpl implements CreditScoreUserDetailsTypePageService {

    @Resource
    private CreditScoreUserDetailsTypePageMapper creditScoreUserDetailsTypePageMapper;

    @Override
    public Page<CreditScoreUserDetailsWithTypesVo> page(CreditScoreUserDetailsPageQueryDto queryDto) {
        int pn = queryDto.getPageNo() == null ? 1 : queryDto.getPageNo();
        int ps = queryDto.getPageSize() == null ? 10 : queryDto.getPageSize();
        if (pn < 1) {
            pn = 1;
        }
        if (ps < 1) {
            ps = 1;
        }

        Page<CreditScoreUserDetailsWithTypesVo> page = new Page<>(pn, ps);
        int offset = (pn - 1) * ps;
        queryDto.setPageNo(offset);
        queryDto.setPageSize(ps);

        Long countUsers = creditScoreUserDetailsTypePageMapper.selectUserCount(queryDto);
        page.setCount(countUsers == null ? 0L : countUsers);

        if (countUsers == null || countUsers <= 0) {
            page.setList(Collections.emptyList());
            return page;
        }

        List<CreditScoreUserDetailsVo> users = creditScoreUserDetailsTypePageMapper.selectUserPage(queryDto);
        if (users == null || users.isEmpty()) {
            page.setList(Collections.emptyList());
            return page;
        }

        List<String> userIds = new ArrayList<>();
        for (CreditScoreUserDetailsVo u : users) {
            if (u != null && u.getUserId() != null) {
                userIds.add(u.getUserId());
            }
        }
        if (userIds.isEmpty()) {
            page.setList(Collections.emptyList());
            return page;
        }

        List<CreditScoreUserTypeItemVo> typeRows = creditScoreUserDetailsTypePageMapper
                .selectTypeRowsByUserIds(userIds, queryDto.getType(), queryDto.getSubtype());

        Map<String, CreditScoreUserDetailsWithTypesVo> map = new HashMap<>();
        for (CreditScoreUserDetailsVo u : users) {
            CreditScoreUserDetailsWithTypesVo vo = new CreditScoreUserDetailsWithTypesVo();
            vo.setUserId(u.getUserId());
            vo.setIdno(u.getIdno());
            vo.setLianghao(u.getLianghao());
            vo.setTotalCreditScore(u.getTotalCreditScore());
            vo.setCreditStatus(u.getCreditStatus());
            map.put(u.getUserId(), vo);
        }

        if (typeRows != null) {
            for (CreditScoreUserTypeItemVo row : typeRows) {
                if (row == null || row.getUserId() == null) {
                    continue;
                }
                CreditScoreUserDetailsWithTypesVo vo = map.get(row.getUserId());
                if (vo != null) {
                    vo.getTypes().add(row);
                }
            }
        }

        // 保持分页顺序：按 users 的顺序输出
        List<CreditScoreUserDetailsWithTypesVo> out = new ArrayList<>(users.size());
        for (CreditScoreUserDetailsVo u : users) {
            CreditScoreUserDetailsWithTypesVo vo = map.get(u.getUserId());
            if (vo != null) {
                out.add(vo);
            }
        }
        page.setList(out);
        return page;
    }

    @Override
    public CreditScoreUserDetailsWithTypesVo getDetailWithTypesByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        CreditScoreUserDetailsPageQueryDto q = new CreditScoreUserDetailsPageQueryDto();
        q.setUserId(userId);
        // 复用分页 SQL：只取一条主表记录
        q.setPageNo(0);
        q.setPageSize(1);

        List<CreditScoreUserDetailsVo> users = creditScoreUserDetailsTypePageMapper.selectUserPage(q);
        if (users == null || users.isEmpty() || users.get(0) == null) {
            return null;
        }

        CreditScoreUserDetailsVo u = users.get(0);
        CreditScoreUserDetailsWithTypesVo vo = new CreditScoreUserDetailsWithTypesVo();
        vo.setUserId(u.getUserId());
        vo.setIdno(u.getIdno());
        vo.setLianghao(u.getLianghao());
        vo.setTotalCreditScore(u.getTotalCreditScore());
        vo.setCreditStatus(u.getCreditStatus());

        List<CreditScoreUserTypeItemVo> typeRows = creditScoreUserDetailsTypePageMapper
                .selectTypeRowsByUserIds(Collections.singletonList(userId), null, null);
        if (typeRows != null) {
            for (CreditScoreUserTypeItemVo row : typeRows) {
                if (row != null) {
                    vo.getTypes().add(row);
                }
            }
        }
        return vo;
    }
}

