package com.seekweb4.chat.modules.exchangeApply.service.impl;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.assetAdmin.dto.ExchangeApplyQueryDto;
import com.seekweb4.chat.modules.exchangeApply.entity.ExchangeApply;
import com.seekweb4.chat.modules.exchangeApply.mapper.ExchangeApplyMapper;
import com.seekweb4.chat.modules.exchangeApply.service.ExchangeApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 靓号兑换申请 Service 实现（仅查询，不提供新增）
 *
 * @author system
 */
@Service
@Transactional(readOnly = true)
public class ExchangeApplyServiceImpl implements ExchangeApplyService {

    @Autowired
    private ExchangeApplyMapper exchangeApplyMapper;

    @Override
    public Page<ExchangeApply> getPage(ExchangeApplyQueryDto queryDto) {
        Page<ExchangeApply> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(convertOrderByToUnderscore(queryDto.getOrderBy()));
        }
        Long count = exchangeApplyMapper.selectAdminCount(queryDto);
        page.setCount(count);
        List<ExchangeApply> list = exchangeApplyMapper.selectAdminPageList(queryDto);
        page.setList(list != null ? list : new ArrayList<>());
        return page;
    }

    @Override
    public ExchangeApply getById(Long id) {
        return exchangeApplyMapper.selectByPrimaryKey(id);
    }

    private String convertOrderByToUnderscore(String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return orderBy;
        }
        String[] parts = orderBy.split(",");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (StringUtils.isBlank(part)) {
                continue;
            }
            String[] fieldAndOrder = part.split("\\s+");
            if (fieldAndOrder.length > 0) {
                String fieldName = fieldAndOrder[0].trim();
                String underscoreField = fieldName.contains("_") ? fieldName : StringUtils.toUnderScoreCase(fieldName);
                if (i > 0) {
                    result.append(", ");
                }
                result.append(underscoreField);
                if (fieldAndOrder.length > 1) {
                    result.append(" ").append(fieldAndOrder[1].trim());
                }
            }
        }
        return result.toString();
    }
}
