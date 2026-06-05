package com.seekweb4.chat.modules.creditScore.service.impl;

import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.creditScore.dto.CreditScoreTypeConfigQueryDto;
import com.seekweb4.chat.modules.creditScore.entity.CreditScoreTypeConfig;
import com.seekweb4.chat.modules.creditScore.mapper.CreditScoreTypeConfigMapper;
import com.seekweb4.chat.modules.creditScore.service.CreditScoreTypeConfigService;
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
public class CreditScoreTypeConfigServiceImpl implements CreditScoreTypeConfigService {
    private static final Map<String, String> ORDER_BY_COLUMNS = new HashMap<>();
    static {
        ORDER_BY_COLUMNS.put("id", "id");
        ORDER_BY_COLUMNS.put("createTime", "create_time");
        ORDER_BY_COLUMNS.put("updateTime", "update_time");
        ORDER_BY_COLUMNS.put("type", "type");
        ORDER_BY_COLUMNS.put("subtype", "subtype");
        ORDER_BY_COLUMNS.put("maxLimit", "max_limit");
        ORDER_BY_COLUMNS.put("score", "score");
        ORDER_BY_COLUMNS.put("status", "status");
        ORDER_BY_COLUMNS.put("orderNum", "order_num");
        ORDER_BY_COLUMNS.put("constituteShow", "constitute_show");
    }

    @Resource
    private CreditScoreTypeConfigMapper creditScoreTypeConfigMapper;

    @Override
    public Page<CreditScoreTypeConfig> page(CreditScoreTypeConfigQueryDto queryDto) {
        queryDto.setOrderBy(normalizeOrderBy(queryDto.getOrderBy()));
        Page<CreditScoreTypeConfig> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        Long count = creditScoreTypeConfigMapper.selectAdminCount(queryDto);
        page.setCount(count);
        List<CreditScoreTypeConfig> list = creditScoreTypeConfigMapper.selectAdminPageList(queryDto);
        page.setList(list);
        return page;
    }

    @Override
    public CreditScoreTypeConfig getById(Long id) {
        return creditScoreTypeConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean save(CreditScoreTypeConfig config) {
        Date now = new Date();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        if (config.getIsDeleted() == null) {
            config.setIsDeleted(0);
        }
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        if (config.getOrderNum() == null) {
            config.setOrderNum(0);
        }
        if (config.getConstituteShow() == null) {
            config.setConstituteShow(1);
        }
        if (config.getSubtype() == null) {
            config.setSubtype(0);
        }
        return creditScoreTypeConfigMapper.insert(config) > 0;
    }

    @Override
    public boolean update(CreditScoreTypeConfig config) {
        config.setUpdateTime(new Date());
        return creditScoreTypeConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status, String updateBy) {
        CreditScoreTypeConfig config = new CreditScoreTypeConfig();
        config.setId(id);
        config.setStatus(status);
        config.setUpdateBy(updateBy);
        config.setUpdateTime(new Date());
        return creditScoreTypeConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public boolean remove(Long id, String updateBy) {
        CreditScoreTypeConfig config = new CreditScoreTypeConfig();
        config.setId(id);
        config.setIsDeleted(1);
        config.setUpdateBy(updateBy);
        config.setUpdateTime(new Date());
        return creditScoreTypeConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public CreditScoreTypeConfig getEnabledByTypeSubtype(Integer type, Integer subtype) {
        if (type == null) {
            return null;
        }
        if (subtype == null) {
            subtype = 0;
        }
        return creditScoreTypeConfigMapper.selectEnabledByTypeSubtype(type, subtype);
    }

    @Override
    public List<CreditScoreTypeConfig> listAllTypes() {
        return creditScoreTypeConfigMapper.selectAllEnabledAndDisabled();
    }

    @Override
    public List<CreditScoreTypeConfig> listEnabledTypes() {
        return creditScoreTypeConfigMapper.selectAllEnabled();
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
}

