package com.seekweb4.chat.modules.live.service.impl;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.persistence.Page;
import com.seekweb4.chat.modules.live.dto.LiveConfigSelectOptionVo;
import com.seekweb4.chat.modules.live.dto.LiveTimeDurationConfigQueryDto;
import com.seekweb4.chat.modules.live.dto.LiveUserTierConfigQueryDto;
import com.seekweb4.chat.modules.live.entity.LiveTimeDurationConfig;
import com.seekweb4.chat.modules.live.entity.LiveUserTierConfig;
import com.seekweb4.chat.modules.live.mapper.LiveTimeDurationConfigMapper;
import com.seekweb4.chat.modules.live.mapper.LiveUserTierConfigMapper;
import com.seekweb4.chat.modules.live.service.LiveConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class LiveConfigServiceImpl implements LiveConfigService {

    @Resource
    private LiveTimeDurationConfigMapper durationConfigMapper;

    @Resource
    private LiveUserTierConfigMapper tierConfigMapper;

    private static final Set<String> CAMEL_CASE_COLUMN_WHITELIST = new HashSet<>();

    @Override
    public Page<LiveTimeDurationConfig> pageDuration(LiveTimeDurationConfigQueryDto queryDto) {
        Page<LiveTimeDurationConfig> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(convertOrderByToUnderscore(queryDto.getOrderBy()));
        }
        Long count = durationConfigMapper.selectAdminCount(queryDto);
        page.setCount(count);
        page.setList(durationConfigMapper.selectAdminPageList(queryDto));
        return page;
    }

    @Override
    public LiveTimeDurationConfig getDurationById(Long id) {
        return durationConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean createDuration(LiveTimeDurationConfig config) {
        config.setIsDeleted(0);
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        config.setDurationSort(10);
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        return durationConfigMapper.insert(config) > 0;
    }

    @Override
    public boolean updateDuration(LiveTimeDurationConfig config) {
        config.setUpdateTime(new Date());
        return durationConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public boolean deleteDuration(Long id, String updateBy) {
        LiveTimeDurationConfig config = new LiveTimeDurationConfig();
        config.setId(id);
        config.setIsDeleted(1);
        config.setUpdateBy(updateBy);
        config.setUpdateTime(new Date());
        return durationConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public List<LiveTimeDurationConfig> listEnabledDurations() {
        return durationConfigMapper.listEnabled();
    }

    @Override
    public List<LiveConfigSelectOptionVo> listDurationSelectOptions() {
        return durationConfigMapper.listSelectOptions();
    }

    @Override
    public Page<LiveUserTierConfig> pageTier(LiveUserTierConfigQueryDto queryDto) {
        Page<LiveUserTierConfig> page = new Page<>(queryDto.getPageNo(), queryDto.getPageSize());
        queryDto.setPageNo((queryDto.getPageNo() - 1) * queryDto.getPageSize());
        if (StringUtils.isNotBlank(queryDto.getOrderBy())) {
            queryDto.setOrderBy(convertOrderByToUnderscore(queryDto.getOrderBy()));
        }
        Long count = tierConfigMapper.selectAdminCount(queryDto);
        page.setCount(count);
        page.setList(tierConfigMapper.selectAdminPageList(queryDto));
        return page;
    }

    @Override
    public LiveUserTierConfig getTierById(Long id) {
        return tierConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean createTier(LiveUserTierConfig config) {
        config.setIsDeleted(0);
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        config.setTierSort(3);
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        return tierConfigMapper.insert(config) > 0;
    }

    @Override
    public boolean updateTier(LiveUserTierConfig config) {
        config.setUpdateTime(new Date());
        return tierConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public boolean deleteTier(Long id, String updateBy) {
        LiveUserTierConfig config = new LiveUserTierConfig();
        config.setId(id);
        config.setIsDeleted(1);
        config.setUpdateBy(updateBy);
        config.setUpdateTime(new Date());
        return tierConfigMapper.updateByPrimaryKeySelective(config) > 0;
    }

    @Override
    public List<LiveUserTierConfig> listEnabledTiers() {
        return tierConfigMapper.listEnabled();
    }

    @Override
    public List<LiveConfigSelectOptionVo> listTierSelectOptions() {
        return tierConfigMapper.listSelectOptions();
    }

    private String convertOrderByToUnderscore(String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            return orderBy;
        }
        String[] parts = orderBy.split(",");
        StringBuilder result = new StringBuilder();
        for (String p : parts) {
            String part = p == null ? "" : p.trim();
            if (StringUtils.isBlank(part)) {
                continue;
            }
            String[] fieldAndOrder = part.split("\\s+");
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
        return result.toString();
    }
}

