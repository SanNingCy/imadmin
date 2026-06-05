package com.seekweb4.chat.modules.creditScore.service.impl;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreLogQueryDto;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreLogMapper;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreLogService;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreLogDetailVo;
import com.seekweb4.chat.modules.creditScore.vo.CreditScoreLogUserSummaryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CreditScoreLogServiceImpl implements CreditScoreLogService {
    private static final Map<String, String> ORDER_BY_COLUMNS = new HashMap<>();
    static {
        ORDER_BY_COLUMNS.put("id", "a.id");
        ORDER_BY_COLUMNS.put("createTime", "a.create_time");
        ORDER_BY_COLUMNS.put("updateTime", "a.update_time");
        ORDER_BY_COLUMNS.put("type", "a.type");
        ORDER_BY_COLUMNS.put("subtype", "a.subtype");
        ORDER_BY_COLUMNS.put("score", "a.score");
        ORDER_BY_COLUMNS.put("userId", "a.user_id");
        ORDER_BY_COLUMNS.put("logDesc", "a.log_desc");
        ORDER_BY_COLUMNS.put("remark", "a.remark");
        ORDER_BY_COLUMNS.put("vipBonusRate", "a.vip_bonus_rate");
        ORDER_BY_COLUMNS.put("lianghaoBonusRate", "a.lianghao_bonus_rate");
        ORDER_BY_COLUMNS.put("baseScore", "a.base_score");
        ORDER_BY_COLUMNS.put("idno", "m.idno");
        ORDER_BY_COLUMNS.put("lianghao", "m.lianghao");
        ORDER_BY_COLUMNS.put("totalCreditScore", "total_credit_score");
    }

    @Resource
    private CreditScoreLogMapper creditScoreLogMapper;

    @Override
    public CreditScoreLogUserSummaryVo getUserSummary(String userId) {
        return creditScoreLogMapper.selectUserSummary(userId);
    }

    @Override
    public String resolveUserIdForSummary(String idno, String lianghao) {
        if (StringUtils.isBlank(idno) && StringUtils.isBlank(lianghao)) {
            return null;
        }
        return creditScoreLogMapper.selectFirstUserIdByIdnoOrLianghao(idno, lianghao);
    }

    @Override
    public CreditScoreLogDetailVo getDetailById(Long id) {
        if (id == null) {
            return null;
        }
        return creditScoreLogMapper.selectDetailById(id);
    }

    @Override
    public Page<CreditScoreLogDetailVo> pageByUser(CreditScoreLogQueryDto queryDto) {
        int pn = queryDto.getPageNo() == null ? 1 : queryDto.getPageNo();
        int ps = queryDto.getPageSize() == null ? 10 : queryDto.getPageSize();
        queryDto.setOrderBy(normalizeOrderBy(queryDto.getOrderBy()));
        Page<CreditScoreLogDetailVo> page = new Page<>(pn, ps);
        queryDto.setPageNo((pn - 1) * ps);
        queryDto.setPageSize(ps);
        Long count = creditScoreLogMapper.selectCount(queryDto);
        page.setCount(count == null ? 0L : count);
        List<CreditScoreLogDetailVo> list = creditScoreLogMapper.selectPageList(queryDto);
        page.setList(list);
        return page;
    }

    private String normalizeOrderBy(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(", ");
        String[] items = raw.split(",");
        for (String item : items) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            String[] parts = item.trim().split("\\s+");
            if (parts.length == 0) {
                continue;
            }
            String column = ORDER_BY_COLUMNS.get(parts[0]);
            if (column == null) {
                continue;
            }
            String direction = "desc";
            if (parts.length > 1 && "asc".equalsIgnoreCase(parts[1])) {
                direction = "asc";
            }
            joiner.add(column + " " + direction);
        }
        return joiner.length() == 0 ? null : joiner.toString();
    }
}
