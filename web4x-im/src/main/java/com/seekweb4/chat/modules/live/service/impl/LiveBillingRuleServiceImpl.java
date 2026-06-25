package com.seekweb4.chat.modules.live.service.impl;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.live.dto.LiveBillingRuleQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveBillingRule;
import com.seekweb4.chat.modules.live.mapper.LiveBillingRuleMapper;
import com.seekweb4.chat.modules.live.service.LiveBillingRuleService;
import com.seekweb4.chat.modules.live.service.LiveUsdtMeetingPriceConfigService;
import com.seekweb4.chat.modules.live.util.LiveAmountRoundingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class LiveBillingRuleServiceImpl implements LiveBillingRuleService {

    @Resource
    private LiveBillingRuleMapper billingRuleMapper;

    @Resource
    private LiveUsdtMeetingPriceConfigService usdtMeetingPriceConfigService;

    private static final Set<String> CAMEL_CASE_COLUMN_WHITELIST = new HashSet<>();

    @Override
    public Page<LiveBillingRule> page(LiveBillingRuleQueryDto queryDto) {
        Page<LiveBillingRule> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());

        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(convertOrderByToUnderscore(queryDto.getOrderBy()));
        }

        Long count = billingRuleMapper.selectAdminCount(queryDto);
        page.setCount(count);
        List<LiveBillingRule> list = billingRuleMapper.selectAdminPageList(queryDto);
        page.setList(list);
        return page;
    }

    @Override
    public LiveBillingRule getById(Long id) {
        return billingRuleMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean create(LiveBillingRule rule) {
        if (rule.getUnitPrice() == null) {
            throw new IllegalArgumentException("单价不能为空");
        }
        validateUnitPrice(rule.getUnitPrice());
        rule.setIsDeleted(0);
        if (rule.getStatus() == null) {
            rule.setStatus(1);
        }
        rule.setCreateTime(new Date());
        rule.setUpdateTime(new Date());
        return billingRuleMapper.insert(rule) > 0;
    }

    @Override
    public boolean update(LiveBillingRule rule) {
        if (rule.getUnitPrice() != null) {
            validateUnitPrice(rule.getUnitPrice());
        }
        rule.setUpdateTime(new Date());
        return billingRuleMapper.updateByPrimaryKeySelective(rule) > 0;
    }

    @Override
    public boolean delete(Long id, String updateBy) {
        LiveBillingRule rule = new LiveBillingRule();
        rule.setId(id);
        rule.setIsDeleted(1);
        rule.setUpdateBy(updateBy);
        rule.setUpdateTime(new Date());
        return billingRuleMapper.updateByPrimaryKeySelective(rule) > 0;
    }

    @Override
    public LiveBillingRule getActiveRule() {
        return billingRuleMapper.selectActiveOne();
    }

    @Override
    public BigDecimal calcAmount(Integer durationMinutes, Integer tierValue) {
        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("durationMinutes 必须大于 0");
        }
        BigDecimal usdtAmount = usdtMeetingPriceConfigService.findSalePriceUsdtByValue(durationMinutes, tierValue);
        if (usdtAmount != null) {
            return usdtAmount;
        }
        LiveBillingRule rule = getActiveRule();
        if (rule == null || rule.getUnitPrice() == null) {
            throw new IllegalStateException("未配置启用的 USDT 会议价格或计费规则");
        }
        BigDecimal amount = rule.getUnitPrice().multiply(new BigDecimal(durationMinutes));
        return LiveAmountRoundingUtil.round(amount, rule.getRoundingRule());
    }

    /**
     * 校验单价：必须大于 0（不能为 0 或负数）
     */
    private void validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("单价不能为空");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("单价必须大于 0，不能为 0 或负数");
        }
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
                String underscoreField;
                if (fieldName.contains("_") || CAMEL_CASE_COLUMN_WHITELIST.contains(fieldName)) {
                    underscoreField = fieldName;
                } else {
                    underscoreField = StringUtils.toUnderScoreCase(fieldName);
                }

                if (result.length() > 0) {
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
