package com.seekweb4.chat.modules.creditScore.service.impl;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreConfig;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreConfigMapper;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@Service
@Transactional
public class CreditScoreConfigServiceImpl implements CreditScoreConfigService {
    private static final Map<String, String> ORDER_BY_COLUMNS = new HashMap<>();
    static {
        ORDER_BY_COLUMNS.put("id", "id");
        ORDER_BY_COLUMNS.put("createTime", "create_time");
        ORDER_BY_COLUMNS.put("updateTime", "update_time");
        ORDER_BY_COLUMNS.put("initScore", "init_score");
        ORDER_BY_COLUMNS.put("vipBonusRate", "vip_bonus_rate");
        ORDER_BY_COLUMNS.put("lianghaoBonusRate", "lianghao_bonus_rate");
        ORDER_BY_COLUMNS.put("price", "price");
        ORDER_BY_COLUMNS.put("priceUsdt", "price_usdt");
        ORDER_BY_COLUMNS.put("scoreInfo", "score_info");
    }

    @Resource
    private CreditScoreConfigMapper creditScoreConfigMapper;

    @Override
    public Page<CreditScoreConfig> page(CreditScoreConfigQueryDto queryDto) {
        int pn = queryDto.getPageNo() == null ? 1 : queryDto.getPageNo();
        int ps = queryDto.getPageSize() == null ? 10 : queryDto.getPageSize();
        queryDto.setOrderBy(normalizeOrderBy(queryDto.getOrderBy()));
        Page<CreditScoreConfig> page = new Page<>(pn, ps);
        queryDto.setPageNo((pn - 1) * ps);
        queryDto.setPageSize(ps);
        Long count = creditScoreConfigMapper.selectAdminCount(queryDto);
        page.setCount(count == null ? 0L : count);
        page.setList(selectPageListSafe(queryDto));
        return page;
    }

    @Override
    public CreditScoreConfig getCurrent() {
        return selectOneSafe(() -> creditScoreConfigMapper.selectCurrent(),
                () -> creditScoreConfigMapper.selectCurrentLegacy());
    }

    @Override
    public CreditScoreConfig getById(Long id) {
        return selectOneSafe(() -> creditScoreConfigMapper.selectByPrimaryKey(id),
                () -> creditScoreConfigMapper.selectByPrimaryKeyLegacy(id));
    }

    @Override
    public boolean save(CreditScoreConfig config) {
        Date now = new Date();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        if (config.getIsDeleted() == null) {
            config.setIsDeleted(0);
        }
        return creditScoreConfigMapper.insert(config) > 0;
    }

    @Override
    public boolean update(CreditScoreConfig config) {
        config.setUpdateTime(new Date());
        return creditScoreConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public boolean remove(Long id, String updateBy) {
        CreditScoreConfig config = new CreditScoreConfig();
        config.setId(id);
        config.setIsDeleted(1);
        config.setUpdateBy(updateBy);
        config.setUpdateTime(new Date());
        return creditScoreConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    private String normalizeOrderBy(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(", ");
        String[] items = raw.split(",");
        for (String item : items) {
            if (item == null || item.trim().isEmpty()) {
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

    private List<CreditScoreConfig> selectPageListSafe(CreditScoreConfigQueryDto queryDto) {
        try {
            return creditScoreConfigMapper.selectAdminPageList(queryDto);
        } catch (Exception e) {
            if (!isMissingPriceUsdtColumn(e)) {
                throw e;
            }
            log.warn("t_credit_score_config.price_usdt 列不存在，列表使用兼容查询；请执行 sql/credit_score_config_price_usdt.sql");
            return creditScoreConfigMapper.selectAdminPageListLegacy(sanitizeLegacyOrderBy(queryDto));
        }
    }

    private CreditScoreConfig selectOneSafe(java.util.function.Supplier<CreditScoreConfig> primary,
                                           java.util.function.Supplier<CreditScoreConfig> legacy) {
        try {
            return primary.get();
        } catch (Exception e) {
            if (!isMissingPriceUsdtColumn(e)) {
                throw e;
            }
            log.warn("t_credit_score_config.price_usdt 列不存在，详情使用兼容查询；请执行 sql/credit_score_config_price_usdt.sql");
            return legacy.get();
        }
    }

    private CreditScoreConfigQueryDto sanitizeLegacyOrderBy(CreditScoreConfigQueryDto queryDto) {
        String orderBy = queryDto.getOrderBy();
        if (orderBy != null && orderBy.contains("price_usdt")) {
            queryDto.setOrderBy(orderBy.replace("price_usdt", "id"));
        }
        return queryDto;
    }

    private boolean isMissingPriceUsdtColumn(Throwable e) {
        while (e != null) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("price_usdt")) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }
}

